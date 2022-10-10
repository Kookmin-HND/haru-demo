import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";

export const path = "/posts";
export const router = Router();

//postid 이후 최근 게시물 50개 정보 sns fragment에서 보여지는 정보
router.get(
  "/recent/:postId",
  (req: Request, res: Response, next: NextFunction) => {
    res.send("test!!");
  }
);

//게시물 하나의 정보 조회
router.post("/", async function (req: Request, res: Response) {
  res.json("");
});

//게시물 입력
router.post("/", async function (req: Request, res: Response) {
  res.json("");
});

//게시물 삭제요청
router.delete("/", async function (req: Request, res: Response) {
  res.json("");
});

//게시물 수정
router.post("/", async function (req: Request, res: Response) {
  res.json("");
});
