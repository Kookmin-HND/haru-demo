import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";
// import { Equal } from "typeorm";

export const path = "/users";
export const router = Router();

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
router.post("/", async function (req: Request, res: Response) {});

// user 회원가입
router.post("/", async function (req: Request, res: Response) {});

// user 정보수정
router.patch("/", async function (req: Request, res: Response) {});

// user 회원탈퇴
router.delete("/", async function (req: Request, res: Response) {});
