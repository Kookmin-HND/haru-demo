import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";

export const path = "/todos";
export const router = Router();

//http://localhost:8000/api/todos
router.get("/", async function (req: Request, res: Response) {
  const users = await myDataSource.getRepository(User).findBy(req.body.email);
  res.json(users);
});
