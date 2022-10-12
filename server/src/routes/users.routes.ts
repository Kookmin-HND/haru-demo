import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";

export const path = "/users";
export const router = Router();

//http://localhost:8000/api/users/ ~~

// user 로그인 기능
router.post("/", async function (req: Request, res: Response) {});

// user 회원가입
router.post("/", async function (req: Request, res: Response) {});

// user 정보수정
router.patch("/", async function (req: Request, res: Response) {});

// user 정보조회
router.get("/", async function (req: Request, res: Response) {});

// user 회원탈퇴
router.delete("/", async function (req: Request, res: Response) {});
