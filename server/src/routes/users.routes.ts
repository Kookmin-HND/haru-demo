import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";
import { Equal } from "typeorm";
import crypto from "crypto"; // hashing 처리를 위한 라이브러리

export const path = "/users";
export const router = Router();

interface UserParams {
  email: string;
}

interface UserSignBody {
  id: Number | null;
  email: string;
  password: string;
  name: string;
}

//http://localhost:8000/api/users/ ~~

// user 정보조회
router.get("/:email", async function (req: Request<UserParams>, res: Response) {
  console.log("get : ", req.params.email);

  try {
    const result = await myDataSource
      .getRepository(User)
      .findOneByOrFail({ email: req.params.email });
    return res.send(result);
  } catch (err) {
    console.log(err);
    return res.status(400).json("No user found");
  }
});

// user 회원가입
router.post(
  "/signup",
  async function (req: Request<{}, {}, UserSignBody>, res: Response) {
    const { email, password, name } = req.body;

    // email, password, name 중 입력되지 않은 것을 확인
    if (!email || !password || !name) {
      console.log("emailm password, name 하나가 비었다.");
      return res.status(400).send("email, password, name을 다시 확인해주세요.");
    }

    // email 중복 확인을 위한 변수
    const overlap_check = await myDataSource
      .getRepository(User)
      .findOneBy({ email: Equal(email) });

    if (overlap_check) {
      // overlap_check가 null이 아니면 중복
      console.log("email 중복");
      return res.status(400).send("중복된 email 입니다.");
    }

    // password hashing 처리
    const salt = crypto.randomBytes(64).toString("base64"); // salt 생성
    const hashedPW = crypto
      .createHash("sha512")
      .update(password + salt)
      .digest("base64");

    // 데이터 생성 후 저장
    const result = await myDataSource.getRepository(User).create({
      email: email,
      password: hashedPW,
      name: name,
      user_salt: salt,
    });

    await myDataSource.getRepository(User).save(result);
    console.log("signup success");
    return res.send(result);
  }
);

// user 로그인 기능
router.post(
  "/login",
  async function (req: Request<{}, {}, UserSignBody>, res: Response) {
    const email = req.body.email;
    const password = req.body.password;
    const user = await myDataSource
      .getRepository(User)
      .findOneBy({ email: Equal(email) });

    if (!user) {
      // user가 null값이면 회원정보가 없는 상태
      console.log("해당 user 없음!");
      return res.status(400).send("사용자 정보가 없습니다.");
    }

    const salt = user.user_salt;
    const hashedPW = crypto
      .createHash("sha512")
      .update(password + salt)
      .digest("base64");

    if (hashedPW != user.password) {
      console.log("비밀번호 틀림");
      return res.status(400).send("비밀번호 틀림");
    } else {
      console.log("로그인 성공");
      return res.send(user);
    }
  }
);

// user 정보수정
router.patch(
  "/changeInfo",
  async function (req: Request<{}, {}, UserSignBody>, res: Response) {
    const user = await myDataSource
      .getRepository(User)
      .findOneBy({ email: Equal(req.body.email) });

    if (!user) {
      // user 정보 유무 확인
      return res.status(400).send("없는 사용자입니다.");
    }
    const changePW = req.body.password;
    const changeName = req.body.name;

    if (!changePW || !changeName) {
      // 변경할 비밀번호와 이름이 비어있는지 확인
      console.log("변경할 비밀번호와 이름을 확인해주세요.");
      return res
        .status(400)
        .send("변경할 비밀번호와 이름을 다시 확인해주세요.");
    }

    const salt = crypto.randomBytes(64).toString("base64"); // hashing 과정에서 사용할 salt 재할당
    const changeHashedPW = crypto
      .createHash("sha512")
      .update(changePW + salt)
      .digest("base64");

    const result = await myDataSource.getRepository(User).update(user.id, {
      // id기반으로 update
      password: changeHashedPW,
      name: changeName,
      user_salt: salt,
    });

    return res.send({
      ...user,
      password: changeHashedPW,
      name: changeName,
      user_salt: salt,
    });
  }
);

// user 회원탈퇴
router.delete(
  "/:email",
  async function (req: Request<UserParams>, res: Response) {
    const email = req.params.email;

    const check = await myDataSource
      .getRepository(User)
      .findOneBy({ email: Equal(email) });

    // user 정보 유무 확인
    if (!check) return res.status(400).send("사용자 정보가 없습니다.");

    const result = await myDataSource // email 또한 unique하기에 email기반 삭제
      .getRepository(User)
      .delete({ email: email });
    console.log("Successfully deleted");
    return res.send(result);

    // merge 후에 해당 user의 게시물과 todo도 삭제해야 한다.
  }
);

// user 정보 전체 출력 (test용)
router.get("/", async function (req: Request, res: Response) {
  const all_users = await myDataSource.getRepository(User).find();
  return res.json(all_users);
});

// 현재 로그인 상태인지 확인 할 수 있는 방법으로 JWT 또는 다른 것 찾아서
// user delete, user changeInfo 등에 적용시키기
