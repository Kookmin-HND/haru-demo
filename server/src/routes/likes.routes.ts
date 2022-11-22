import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import DB from "../app-data-source";
import { Post } from "../entity/post";
import { PostLike } from "../entity/postLike";
import { Equal } from "typeorm";
import { CommentLike } from "../entity/commentLike";

export const path = "/likes";
export const router = Router();


//게시물 좋아요 입력
router.post("/post", async (req: Request, res: Response) => {
    const post = DB.getRepository(PostLike).create({ ...req.body, post: req.body.postId });
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



//댓글 좋아요 입력
router.post("/comment", async (req: Request, res: Response) => {
    const comment = DB.getRepository(CommentLike).create({ ...req.body, comment: req.body.commentId});
    const result = await DB.getRepository(CommentLike).save(comment);

    return res.json(result);
});


//댓글 좋아요 취소
router.delete("/comment/:commentId/:user", async (req: Request, res: Response) => {
    const user = req.params.user
    const result = await DB.getRepository(CommentLike).delete({
        comment: Equal(req.params.commentId),
        user
    });
    
    return res.json(result);
});
