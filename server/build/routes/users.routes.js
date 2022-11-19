"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.router = exports.path = void 0;
const express_1 = require("express");
const app_data_source_1 = __importDefault(require("../app-data-source"));
const user_1 = require("../entity/user");
const typeorm_1 = require("typeorm");
const bcrypt_1 = __importDefault(require("bcrypt")); // hashing 처리를 위한 라이브러리
const passport_1 = __importDefault(require("passport"));
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
exports.path = "/users";
exports.router = (0, express_1.Router)();
//http://localhost:8000/api/users/ ~~
// user 정보조회
exports.router.get("/info", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        passport_1.default.authenticate("jwt", (jwtError, user, info) => {
            console.log(jwtError, user, info);
            if (!user || jwtError) {
                return res.status(400).send("re-login");
            }
            user.password = "";
            return res.json({ user });
        })(req, res);
    }
    catch (err) {
        console.log(err);
        return res.status(400).send("No user found");
    }
}));
//user login
exports.router.post("/login", (req, res, next) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        passport_1.default.authenticate("local", (authError, user, info) => {
            console.log(authError, user, info);
            // 인증 실패 또는 user 정보 없을시 에러 발생
            if (authError || !user) {
                return res.status(400).json(info.reason);
            }
            req.login(user, { session: false }, (loginError) => {
                if (loginError) {
                    console.error(loginError);
                    return res.status(400).json(loginError);
                }
                const token = jsonwebtoken_1.default.sign({ email: user.email }, process.env.JWT_KEY);
                return res
                    .cookie("token", token, { httpOnly: true })
                    .json({ message: "토큰 발급 완료" });
            });
        })(req, res);
    }
    catch (error) {
        console.error(error);
    }
}));
// user 회원가입
exports.router.post("/signup", function (req, res) {
    return __awaiter(this, void 0, void 0, function* () {
        let { email, password, name } = req.body;
        // email, password, name 중 입력되지 않은 것을 확인
        if (!email || !password || !name) {
            console.log("emailm password, name 하나가 비었다.");
            return res.status(400).send("email, password, name을 다시 확인해주세요.");
        }
        // email 중복 확인을 위한 변수
        const overlap_check = yield app_data_source_1.default.getRepository(user_1.User).findOneBy({
            email: (0, typeorm_1.Equal)(email),
        });
        if (overlap_check) {
            // overlap_check가 null이 아니면 중복
            console.log("email 중복");
            return res.status(400).send("중복된 email 입니다.");
        }
        // password hashing 처리
        bcrypt_1.default.genSalt(10, (err, salt) => {
            if (err)
                return res.status(500).json("비밀번호 해쉬화에 실패");
            bcrypt_1.default.hash(password, salt, (err, hash) => __awaiter(this, void 0, void 0, function* () {
                if (err)
                    return res.status(500).json("비밀번호 해쉬화에 실패");
                password = hash;
                const result = yield app_data_source_1.default.getRepository(user_1.User).create({
                    email: email,
                    password: password,
                    name: name,
                });
                yield app_data_source_1.default.getRepository(user_1.User).save(result);
                console.log("signup success");
                return res.send(result);
            }));
        });
    });
});
// user 정보수정
// router.patch(
//   "/changeInfo",
//   async function (req: Request<{}, {}, UserSignBody>, res: Response) {
//     const user = await myDataSource
//       .getRepository(User)
//       .findOneBy({ email: Equal(req.body.email) });
//     if (!user) {
//       // user 정보 유무 확인
//       return res.status(400).send("없는 사용자입니다.");
//     }
//     const changePW = req.body.password;
//     const changeName = req.body.name;
//     if (!changePW || !changeName) {
//       // 변경할 비밀번호와 이름이 비어있는지 확인
//       console.log("변경할 비밀번호와 이름을 확인해주세요.");
//       return res
//         .status(400)
//         .send("변경할 비밀번호와 이름을 다시 확인해주세요.");
//     }
//     const salt = crypto.randomBytes(64).toString("base64"); // hashing 과정에서 사용할 salt 재할당
//     const changeHashedPW = crypto
//       .createHash("sha512")
//       .update(changePW + salt)
//       .digest("base64");
//     const result = await myDataSource.getRepository(User).update(user.id, {
//       // id기반으로 update
//       password: changeHashedPW,
//       name: changeName,
//       user_salt: salt,
//     });
//     return res.send({
//       ...user,
//       password: changeHashedPW,
//       name: changeName,
//       user_salt: salt,
//     });
//   }
// );
// user 회원탈퇴
// router.delete(
//   "/:email",
//   async function (req: Request<UserParams>, res: Response) {
//     const email = req.params.email;
//     const check = await myDataSource
//       .getRepository(User)
//       .findOneBy({ email: Equal(email) });
//     // user 정보 유무 확인
//     if (!check) return res.status(400).send("사용자 정보가 없습니다.");
//     const result = await myDataSource // email 또한 unique하기에 email기반 삭제
//       .getRepository(User)
//       .delete({ email: email });
//     console.log("Successfully deleted");
//     return res.send(result);
//     // merge 후에 해당 user의 게시물과 todo도 삭제해야 한다.
//   }
// );
// user 정보 전체 출력 (test용)
// router.get("/", async function (req: Request, res: Response) {
//   const all_users = await myDataSource.getRepository(User).find();
//   return res.json(all_users);
// });
// 현재 로그인 상태인지 확인 할 수 있는 방법으로 JWT 또는 다른 것 찾아서
// user delete, user changeInfo 등에 적용시키기
