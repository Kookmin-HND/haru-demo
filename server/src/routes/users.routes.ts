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

interface UserRequestBody {
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
    return res.status(404).json("No user found");
  }
});

// user 회원가입
router.post(
  "/signup",
  async function (req: Request<{}, {}, UserRequestBody>, res: Response) {
    const { email, password, name } = req.body;

    // email, password, name 중 입력되지 않은 것을 확인
    if (!email || !password || !name) {
      console.log("emailm password, name 하나가 비었다.");
      return res.status(400).send("email, password, name을 다시 확인해주세요.");
    }

    // email 중복 확인을 위한 변수
    const overlap_check = await myDataSource
      .getRepository(User)
      .findBy({ email: Equal(email) });

    if (overlap_check.length) {
      // 길이가 0보다 크면 중복이므로 제외
      console.log("email 중복");
      return res.status(400).send("중복된 email 입니다.");
    }

    // password hashing 처리
    const salt = crypto.randomBytes(64).toString("base64");
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

// user 정보 전체 출력 (test용)
router.get("/", async function (req: Request, res: Response) {
  const users = await myDataSource.getRepository(User).find();
  return res.json(users);
});

// user 로그인 기능
router.post("/login", async function (req: Request, res: Response) {});

// user 정보수정
router.patch("/", async function (req: Request, res: Response) {});

// user 회원탈퇴
router.delete("/", async function (req: Request, res: Response) {});
