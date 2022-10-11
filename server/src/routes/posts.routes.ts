import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { Post } from "../entity/post";
import { MoreThan } from "typeorm";

export const path = "/posts";
export const router = Router();

//postid 이후 최근 게시물 50개 정보 sns fragment에서 보여지는 정보
router.get("/recent/:postId", async function (req: Request, res: Response) {
  //마지막으로 읽은 post Id
  const readedPostId = Number(req.params.postId);
  const results = await myDataSource.getRepository(Post).find({
    where: { id: MoreThan(readedPostId) },
    take: 50,
    order: { id: "DESC" },
  });
  res.json(results);
});

//게시물 하나의 정보 조회
router.get("/:postId", async function (req: Request, res: Response) {
  res.json("");
});

//게시물 입력
router.post("/", async function (req: Request, res: Response) {
  const post = myDataSource.getRepository(Post).create(req.body);
  const results = await myDataSource.getRepository(Post).save(post);
  res.json(results);
});

//게시물 삭제요청
router.delete("/:postId", async function (req: Request, res: Response) {
  res.json("");
});

//게시물 수정
router.patch("/:postId", async function (req: Request, res: Response) {
  res.json("");
});
