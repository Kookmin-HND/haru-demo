import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import myDataSource from "../app-data-source";
import { Todo } from "../entity/todo";
import { TodoLog } from "../entity/todo-log";
import { User } from "../entity/user";

interface TodoParams {
  email: string;
}

interface TodoRequestBody {
  id: number | null;
  folder: string;
  content: string;
  dates: string[];
  days: boolean[];
}

type TodoResponseBody = Todo[];
type Date = string;

export const path = "/todos";
export const router = Router();

// 사용자의 모든 todo를 반환한다.
router.get(
  "/:email",
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {
    const result: TodoResponseBody = [];

    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
    });

    // 위에서 가져온 데이터를 결과값에 추가한다.
    result.push(...todos);

    // 이 사용자가 가지고 있는 모든 todo를 반환한다.
    return res.json(result);
  }
);

// 사용자로부터 folder, content, dates, days를 받아서 todo table에 데이터를 저장한다.
router.post(
  "/:email",
  async (req: Request<TodoParams, {}, TodoRequestBody>, res: Response) => {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    const { folder, content, dates, days } = req.body;

    // 혹시 입력으로부터 빠져있는 값이 있는지 확인한다.
    if (
      !folder ||
      !content ||
      !dates ||
      (dates && !dates.length) ||
      !days ||
      (days && !days.length)
    ) {
      return res
        .status(400)
        .send("folder, content, dates, days 중 하나의 값이 없습니다.");
    }

    // 입력 값에 따른 데이터를 생성한다.
    const todo = await myDataSource.getRepository(Todo).create({
      writer,
      folder,
      content,
      dates: JSON.stringify(dates),
      days: JSON.stringify(days),
    });

    // 위에서 생성한 todo 데이터를 table에 저장한다.
    await myDataSource.getRepository(Todo).save(todo);
    return res.json(todo);
  }
);

// 사용자로부터 입력받은 데이터(folder, content, dates, days)를 해당하는 todo를 id값을 기준으로 찾아 변경한다.
router.patch(
  "/",
  async (
    req: Request<{}, {}, TodoRequestBody & { date: string }>,
    res: Response
  ) => {
    // todo data의 id값을 가져온다.
    const id = req.body.id;
    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다.");
    }

    // request body로부터 데이터를 가져온다.
    const { folder, content, dates, days } = req.body;

    if (
      (folder && !folder.trim()) ||
      (content && !content.trim()) ||
      (dates && !dates.length)
    ) {
      return res
        .status(400)
        .send("folder 또는 content 또는 date가 비어있습니다.");
    }

    // todo 데이터를 업데이트 한다.
    const result = await myDataSource.getRepository(Todo).update(id, {
      folder,
      content,
      dates: JSON.stringify(dates),
      days: JSON.stringify(days),
    });

    return res.json(result);
  }
);

// todo id값을 입력받아 해당하는 데이터를 찾은 후 date를 제거하거나, 추가한다.
// 그리고 date를 제거하게 되면, todo-log에 추가하고,
// 추가하게 된다면, todo-log에 있는지 확인하고, 제거한다.
router.patch(
  "/check",
  async (req: Request<{}, {}, { id: number; date: string }>, res: Response) => {
    // date는 무조건 유효한 일자만 입력으로 주어진다고 가정한다.
    // 이유는, 아이템이 클릭되었을때만 이 API가 호출되는데 해당되는 Todo 아이템은
    // 정상적인 date를 가질 것이다.
    const { id, date } = req.body;

    if (!date || (date && !date.trim())) {
      return res.status(400).send("date가 비어있습니다.");
    }

    const todo = await myDataSource.getRepository(Todo).findOneBy({
      id: Equal(id),
    });

    if (!todo) {
      return res.status(400).send("Todo Data를 찾을 수 없습니다.");
    }

    const todoLog = await myDataSource.getRepository(TodoLog).findOneBy({
      todoId: Equal(id),
      date: Equal(date),
    });

    console.log(`todoLog: ${todoLog}`);
    const result: any[] = [];
    if (todoLog) {
      // Log에 있으므로 삭제한다.
      result.push(
        await myDataSource.getRepository(Todo).update(todo.id, {
          dates: JSON.stringify([...(JSON.parse(todo.dates) as Date[]), date]),
        })
      );

      result.push(await myDataSource.getRepository(TodoLog).delete(todoLog.id));
    } else {
      // Log에 없으므로 추가한다.
      result.push(
        await myDataSource.getRepository(Todo).update(todo.id, {
          dates: JSON.stringify(
            (JSON.parse(todo.dates) as Date[]).filter((v) => v != date)
          ),
        })
      );

      result.push(
        await myDataSource.getRepository(TodoLog).create({
          todoId: id,
          date: date,
        })
      );
      await myDataSource.getRepository(TodoLog).save(result[result.length - 1]);
    }
    return res.json(result);
  }
);

// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
router.delete("/", async (req: Request, res: Response) => {
  const id = req.body.id;

  if (!id) {
    return res.status(400).send("id가 존재하지 않습니다");
  }

  const result = await myDataSource.getRepository(Todo).delete(id);
  return res.json(result);
});
