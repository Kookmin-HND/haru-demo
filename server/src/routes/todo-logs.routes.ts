import { Request, Response, Router } from "express";
import DB from "../app-data-source";
import { TodoLog } from "../entity/todo-log";

export const path = "/todo-logs";
export const router = Router();

// todoId가 일치하는 log들을 불러온다.
// completed 값에 따라 값들을 가져온다.
router.get(
  "/",
  async (
    req: Request<{}, {}, { todoId: number; completed: boolean }>,
    res: Response<TodoLog[]>
  ) => {
    const { todoId, completed } = req.body;
    const logs = await DB.getRepository(TodoLog).findBy({
      todoId,
      completed: completed ?? false,
    });
    return res.json(logs);
  }
);

// todoId가 일치하는 모든 log들을 불러온다.
router.get(
  "/all",
  async (
    req: Request<{}, {}, { todoId: number }>,
    res: Response<TodoLog[]>
  ) => {
    const { todoId } = req.body;
    const logs = await DB.getRepository(TodoLog).findBy({
      todoId,
    });
    return res.json(logs);
  }
);
