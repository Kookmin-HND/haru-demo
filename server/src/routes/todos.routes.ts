import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import DB from "../app-data-source";
import DB from "../app-data-source";
import { Todo } from "../entity/todo";
import { TodoLog } from "../entity/todo-log";

export const path = "/todos";
export const router = Router();

// 사용자의 모든 todo를 반환한다.
// completed 값에 따라 완료여부 값들을 필터링한다.
router.get(
  "/:email",
  async (
    req: Request<{ email: string }, {}, { completed: boolean }>,
    res: Response<Todo[]>
  ) => {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const { email: writer } = req.params;
    const { completed } = req.body;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await DB.getRepository(Todo).findBy({
      writer: writer,
    });

    // completed의 값이 일치하는 todo를 가져온다.
    const result: Todo[] = [];
    for (const todo of todos) {
      const logs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        completed: completed,
      });

      if (logs.length) {
        result.push(todo);
      }
    }

    // 이 사용자가 가지고 있는 조건이 일치하는 모든 todo를 반환한다.
    return res.json(result);
  }
);

// 사용자의 모든 데이터를 가져오기 (todo-log) 포함.
router.get(
  "/:email/all",
  async (
    req: Request<{ email: string }>,
    res: Response<{ [key: number]: { todo: Todo; todoLogs: TodoLog[] } }>
  ) => {
    const writer = req.params.email;

    const todos = await DB.getRepository(Todo).findBy({
      writer: writer,
    });

    const todosMap: { [key: number]: { todo: Todo; todoLogs: TodoLog[] } } = {};
    for (const todo of todos) {
      const todoLogs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
      });

      todosMap[todo.id] = { todo, todoLogs };
    }

    return res.json(todosMap);
  }
);

// 사용자가 작성한 todo 중 folder가 일치하는 todo를 반환한다.
// completed 값에 따라 완료여부를 필터링한다.
router.get(
  "/:email/folder/:folder",
  async (
    req: Request<{ email: string; folder: string }, {}, { completed: boolean }>,
    res: Response<Todo[]>
  ) => {
    const { email: writer, folder } = req.params;
    const { completed } = req.body;

    const todos = await DB.getRepository(Todo).findBy({
      writer: writer,
      folder: folder,
    });

    const result: Todo[] = [];
    for (const todo of todos) {
      const todoLogs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        completed,
      });
      if (todoLogs.length) {
        result.push(todo);
      }
    }
    return res.json(result);
  }
);

// 사용자가 작성한 todo 중 date가 일치하는 모든 todo를 반환한다.
// completed 값에 따라 완료여부를 필터링한다.
router.get(
  "/:email/date/:date",
  async (
    req: Request<{ email: string; date: string }, {}, { completed: boolean }>,
    res: Response<Todo[]>
  ) => {
    const { email: writer, date } = req.params;
    const { completed } = req.body;

    const todos = await DB.getRepository(Todo).findBy({
      writer: writer,
    });

    const result: Todo[] = [];
    for (const todo of todos) {
      const todoLogs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        date: date,
        completed,
      });
      if (todoLogs.length) {
        result.push(todo);
      }
    }

    return res.json(result);
  }
);

// 사용자로부터 folder, content, dates, days를 받아서 todo table에 데이터를 저장한다.
router.post(
  "/:email",
  async (
    req: Request<
      { email: string },
      {},
      { folder: string; content: string; dates: string[]; days: boolean[] }
    >,
    res: Response
  ) => {
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
    const todo = DB.getRepository(Todo).create({
      writer,
      folder,
      content,
      days: JSON.stringify(days),
    });
    // 위에서 생성한 todo 데이터를 table에 저장한다.
    await DB.getRepository(Todo).save(todo);
    result.push(todo);

    // TodoLog에 기록을 추가한다.
    const logs: TodoLog[] = [];
    for (const date of dates) {
      logs.push(
        DB.getRepository(TodoLog).create({
          todoId: todo.id,
          date,
          completed: false,
        })
      );
    }
    await DB.getRepository(TodoLog).save(logs);
    result.push(...logs);
    return res.json(result);
  }
  return res.json(result);
});

// 사용자로부터 입력받은 데이터(folder, content, dates, days)를 해당하는 todo를 id값을 기준으로 찾아 변경한다.
// 그리고 todo-logs에 접근하여 해당 데이터를 삭제, 추가한다.
router.patch(
  "/",
  async (
    req: Request<
      {},
      {},
      {
        id: number;
        folder: string;
        content: string;
        dates: string[];
        days: boolean[];
      }
    >,
    res: Response
  ) => {
    // todo data의 id값을 가져온다.
    const id = req.body.id;
    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다.");
    }

    // request body로부터 데이터를 가져온다.
    const { folder, content, dates, days } = req.body;

    // todo 데이터를 업데이트 한다.
    const result: any[] = [
      await DB.getRepository(Todo).update(id, {
        folder,
        content,
        days: JSON.stringify(days),
      }),
    ];

    // 만약 dates가 있다면 todo-log를 전부 삭제하고, 다시 추가한다.
    // 기존에 있던 것을 비교하여 삭제, 추가보다 전부 삭제, 추가가 더 효율적으로 생각하여 이 방식을 택한다.
    if (dates && dates.length) {
      result.push(
        await DB.getRepository(TodoLog).delete({
          todoId: id,
        })
      );
      console.log(dates);
      for (const date of dates) {
        console.log(date);
        result.push(
          DB.getRepository(TodoLog).create({
            todoId: id,
            date,
            completed: false,
          })
        );
      }
      await DB.getRepository(TodoLog).save(result.slice(2));
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

    const result = await DB.getRepository(TodoLog).update(
      {
        todoId,
        date,
      },
      {
        completed,
      }
    );

  return res.json(result);
});

// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
// 그리고 todo-logs에 접근하여 해당하는 log들을 모두 삭제한다.
router.delete(
  "/",
  async (req: Request<{}, {}, { id: number }>, res: Response) => {
    const id = req.body.id;

    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다");
    }

    const result = await DB.getRepository(Todo).delete(id);
    await DB.getRepository(TodoLog).delete({
      todoId: id,
    });
    return res.json(result);
  }
);
