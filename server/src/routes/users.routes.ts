import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";
import { initializeApp, applicationDefault } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
// import { Equal } from "typeorm";

export const path = "/users";
export const router = Router();

// firebase 설정
// const firebase = require("firebase-admin");
// const config = {
//   apiKey: "AIzaSyDO93PKlhA08OqG16A5uQz9t8rZJs-cKFI",
//   authDomain: "haru-e2a10.firebaseapp.com",
//   projectId: "haru-e2a10",
//   storageBucket: "haru-e2a10.appspot.com",
//   messagingSenderId: "32689857481",
//   appId: "1:32689857481:web:510f5250cad3ff4055be18",
//   measurementId: "G-0ZH177LVGZ",
// };
const app = initializeApp({
  credential: applicationDefault(),
  projectId: "haru-e2a10",
});

const auth = getAuth(app);

interface UserParams {
  email: string;
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

// user 로그인 기능
router.post("/login", async function (req: Request, res: Response) {
  const { email, password } = req.body;
  auth
    .getUserByEmail(email)
    .then((user: any) => {
      console.log("Successfully login");
    })
    .catch((error) => {
      console.log("error login in with Email and PassWord", error);
    });

  // .auth()
  // .signInWithEmailAndPassWord(email, password)
  // .then((user: any) => {
  //   console.log("Successfully login");
  //   return res.send(user);
  // })
  // .catch((error: any) => {
  //   console.log("error login in with Email and PassWord", error);
  //   return res.send(error);
  // });
});

// user 회원가입
router.post("/", async function (req: Request, res: Response) {});

// user 정보수정
router.patch("/", async function (req: Request, res: Response) {});

// user 회원탈퇴
router.delete("/", async function (req: Request, res: Response) {});
