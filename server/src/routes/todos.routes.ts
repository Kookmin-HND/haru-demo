import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { Todo } from "../entity/todo";

export const path = "/todos";
export const router = Router();

//http://localhost:8000/api/todos
router.get("/", async function (req: Request, res: Response) {});

router.post("/", async function (req: Request, res: Response) {
  console.log(req.body);
  const todo = await myDataSource.getRepository(Todo).create(req.body);
  const results = await myDataSource.getRepository(Todo).save(todo);
  return res.send(results);
});
