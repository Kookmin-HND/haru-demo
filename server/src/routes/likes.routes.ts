import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import DB from "../app-data-source";
import { Post } from "../entity/post";
import { PostLike } from "../entity/like";
import { Equal } from "typeorm";

export const path = "/likes";
export const router = Router();


//게시물 좋아요 입력
router.post("/post", async (req: Request, res: Response) => {
  const post = DB.getRepository(PostLike).create({ ...req.body, post: req.body.postId});
  const result = await DB.getRepository(PostLike).save(post);

  return res.json(result);
});


//게시물 좋아요 취소
router.delete("/post/:postId/:user", async (req: Request, res: Response) => {
    const user = req.params.user
    const result = await DB.getRepository(PostLike).delete({
        post: Equal(req.params.postId),
        user
    });
    return res.json(result);
  });
  