import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import myDataSource from "../app-data-source";
import { TodoLog } from "../entity/todo-log";

export const path = "/todo-logs";
export const router = Router();

router.get("/", async (req: Request, res: Response) => {
  const todoId = req.body.todoId;
  const logs = await myDataSource.getRepository(TodoLog).findBy({
    todoId: Equal(todoId),
  });
  return res.json(logs);
});
