import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import myDataSource from "../app-data-source";
import { Todo } from "../entity/todo";
import { TodoLog } from "../entity/todo-log";

interface TodoParams {
  email: string;
  folder: string;
  date: string;
}

interface TodoRequestBody {
  id: number | null;
  folder: string;
  content: string;
  dates: string[];
  days: boolean[];
}

type TodoResponseBody = Todo[];

export const path = "/todos";
export const router = Router();

// 사용자의 모든 todo를 반환한다.
router.get(
  "/:email",
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
    });

    // 이 사용자가 가지고 있는 모든 todo를 반환한다.
    return res.json(todos);
  }
);

// TODO: 사용자의 모든 데이터를 가져오기 (todo-log) 포함.
router.get("/:email/all"),
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {};

// 사용자가 작성한 todo 중 folder가 일치하는 todo를 반환한다.
router.get(
  "/:email/folder/:folder",
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {
    const { email: writer, folder } = req.params;

    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
      folder: Equal(folder),
    });

    return res.json(todos);
  }
);

// 사용자가 작성한 todo 중 date가 일치하는 모든 todo를 반환한다.
router.get(
  "/:email/date/:date",
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {
    const { email: writer, date } = req.params;

    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
    });

    const result: Todo[] = [];
    for (const todo of todos) {
      const todoLogs = await myDataSource
        .getRepository(TodoLog)
        .findAndCountBy({
          todoId: Equal(todo.id),
          date: Equal(date),
        });
      if (todoLogs[1]) {
        result.push(todo);
      }
    }

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

    const result: any[] = [];
    // 입력 값에 따른 데이터를 생성한다.
    const todo = await myDataSource.getRepository(Todo).create({
      writer,
      folder,
      content,
      days: JSON.stringify(days),
    });
    // 위에서 생성한 todo 데이터를 table에 저장한다.
    await myDataSource.getRepository(Todo).save(todo);
    result.push(todo);

    // TodoLog에 기록을 추가한다.
    const logs: TodoLog[] = [];
    for (const date of dates) {
      logs.push(
        await myDataSource.getRepository(TodoLog).create({
          todoId: todo.id,
          date,
          completed: false,
        })
      );
    }
    await myDataSource.getRepository(TodoLog).save(logs);
    result.push(...logs);
    return res.json(result);
  }
);

// 사용자로부터 입력받은 데이터(folder, content, dates, days)를 해당하는 todo를 id값을 기준으로 찾아 변경한다.
// 그리고 todo-logs에 접근하여 해당 데이터를 삭제, 추가한다.
router.patch(
  "/",
  async (req: Request<{}, {}, TodoRequestBody>, res: Response) => {
    // todo data의 id값을 가져온다.
    const id = req.body.id;
    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다.");
    }

    // request body로부터 데이터를 가져온다.
    const { folder, content, dates, days } = req.body;

    // todo 데이터를 업데이트 한다.
    const result: any[] = [
      await myDataSource.getRepository(Todo).update(id, {
        folder,
        content,
        days: JSON.stringify(days),
      }),
    ];

    // 만약 dates가 있다면 todo-log를 전부 삭제하고, 다시 추가한다.
    // 기존에 있던 것을 비교하여 삭제, 추가보다 전부 삭제, 추가가 더 효율적으로 생각하여 이 방식을 택한다.
    if (dates && dates.length) {
      result.push(
        await myDataSource.getRepository(TodoLog).delete({
          todoId: Equal(id),
        })
      );
      console.log(dates);
      for (const date of dates) {
        console.log(date);
        result.push(
          await myDataSource.getRepository(TodoLog).create({
            todoId: id,
            date,
            completed: false,
          })
        );
      }
      await myDataSource.getRepository(TodoLog).save(result.slice(2));
    }
    return res.json(result);
  }
);

// todo-logs DB에 접근하여 입력으로 받은 completed 값으로 바꾸어준다.
router.patch(
  "/check",
  async (
    req: Request<{}, {}, { todoId: number; date: string; completed: boolean }>,
    res: Response
  ) => {
    const { todoId, date, completed } = req.body;

    if (!date || (date && !date.trim())) {
      return res.status(400).send("date가 비어있습니다.");
    }

    const result = await myDataSource.getRepository(TodoLog).update(
      {
        todoId: Equal(todoId),
        date: Equal(date),
      },
      {
        completed,
      }
    );

    return res.json(result);
  }
);

// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
// 그리고 todo-logs에 접근하여 해당하는 log들을 모두 삭제한다.
router.delete("/", async (req: Request, res: Response) => {
  const id = req.body.id;

  if (!id) {
    return res.status(400).send("id가 존재하지 않습니다");
  }

  const result = await myDataSource.getRepository(Todo).delete(id);
  await myDataSource.getRepository(TodoLog).delete({
    todoId: Equal(id),
  });
  return res.json(result);
});
