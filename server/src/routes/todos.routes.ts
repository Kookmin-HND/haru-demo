import { Request, Response, Router } from "express";
import DB from "../app-data-source";
import { Todo } from "../entity/todo";
import { TodoLog } from "../entity/todo-log";

export const path = "/todos";
export const router = Router();

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

    const result: { todo: Todo | null; logs: TodoLog[] } = {
      todo: null,
      logs: [],
    };
    // 입력 값에 따른 데이터를 생성한다.
    const todo = DB.getRepository(Todo).create({
      writer,
      folder,
      content,
      days: JSON.stringify(days),
      deleted: false,
    });
    // 위에서 생성한 todo 데이터를 table에 저장한다.
    await DB.getRepository(Todo).save(todo);
    result.todo = todo;

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
    result.logs = logs;
    return res.json(result);
  }
);

// 사용자의 모든 todo를 folder로 구분하여 반환한다.
// completed 값에 따라 완료여부 값들을 필터링한다.
router.get(
  "/:email",
  async (
    req: Request<{ email: string }, {}, {}, { completed: boolean }>,
    res: Response<{ [key: number]: { todo: Todo; logs: TodoLog[] } }>
  ) => {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const { email: writer } = req.params;
    const { completed } = req.query;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await DB.getRepository(Todo).findBy({
      writer,
    });

    // completed의 값이 일치하는 todo를 가져온다.
    const result: { [key: number]: { todo: Todo; logs: TodoLog[] } } = {};
    for (const todo of todos) {
      const logs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        completed,
      });

      if (logs.length) {
        result[todo.id] = { todo, logs };
      }
    }
    // 이 사용자가 가지고 있는 조건이 일치하는 모든 todo를 반환한다.
    return res.json(result);
  }
);

// 사용자의 모든 todo를 date로 구분하여 반환한다.
// completed 값에 따라 완료여부 값들을 필터링한다.

// 사용자로부터 todoId를 입력받아, 해당 todo의 todo-log를 반환한다.
router.get(
  "/log/:todoId/:completed",
  async (
    req: Request<{ todoId: number; completed: boolean }, {}, {}>,
    res: Response<TodoLog[]>
  ) => {
    const { todoId, completed } = req.params;

    const logs = await DB.getRepository(TodoLog).findBy({
      todoId,
      completed,
    });

    return res.json(logs);
  }
);

// 사용자로부터 todoId를 입력받아, 해당 todo의 completed 상관없이 모두 반환한다.
router.get(
  "/log/:todoId/all",
  async (
    req: Request<{ todoId: number }, {}, {}>,
    res: Response<TodoLog[]>
  ) => {
    const { todoId } = req.params;

    const logs = await DB.getRepository(TodoLog).findBy({
      todoId,
    });
    return res.json(logs);
  }
);

// 사용자가 가지고 있는 모든 todo를 folder로 구분하여 반환한다.
router.get(
  "/:email/folder",
  async (
    req: Request<{ email: string }, {}, {}, { completed: boolean }>,
    res: Response<{ [key: string]: { todos: Todo[]; logs: TodoLog[][] } }>
  ) => {
    const { email: writer } = req.params;
    const { completed } = req.query;

    const todos = await DB.getRepository(Todo).findBy({
      writer,
    });

    const result: { [key: string]: { todos: Todo[]; logs: TodoLog[][] } } = {};
    for (const todo of todos) {
      const logs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        completed,
      });
      if (logs.length) {
        if (todo.folder in result) {
          result[todo.folder].todos.push(todo);
          result[todo.folder].logs.push(logs);
        } else {
          result[todo.folder] = { todos: [todo], logs: [logs] };
        }
      }
    }
    return res.json(result);
  }
);

// 사용자가 작성한 todo 중 folder가 일치하는 todo를 반환한다.
// completed 값에 따라 완료여부를 필터링한다.
router.get(
  "/:email/folder/:folder",
  async (
    req: Request<
      { email: string; folder: string },
      {},
      {},
      { completed: boolean }
    >,
    res: Response<{ todos: Todo[]; logs: TodoLog[][] }>
  ) => {
    const { email: writer, folder } = req.params;
    const { completed } = req.query;

    const todos = await DB.getRepository(Todo).findBy({
      writer,
      folder,
    });

    const result: { todos: Todo[]; logs: TodoLog[][] } = {
      todos: [],
      logs: [],
    };
    for (const todo of todos) {
      const logs = await DB.getRepository(TodoLog).findBy({
        todoId: todo.id,
        completed,
      });
      if (logs.length) {
        result.todos.push(todo);
        result.logs.push(logs);
      }
    }
    return res.json(result);
  }
);

// 사용자가 가지고 있는 todo를 받아온 dates 내 date로 구분하여 반환한다.
router.get(
  "/:email/date",
  async (
    req: Request<
      { email: string },
      {},
      {},
      { completed: boolean; dates: string[] }
    >,
    res: Response<{ [key: string]: { todos: Todo[]; logs: TodoLog[] } }>
  ) => {
    const { email: writer } = req.params;
    const { completed, dates } = req.query;

    const result: { [key: string]: { todos: Todo[]; logs: TodoLog[] } } = {};
    for (const date of dates) {
      const logs = await DB.getRepository(TodoLog).findBy({
        date,
        completed,
      });

      result[date] = { todos: [], logs: [] };
      result[date].logs = logs;
      result[date].todos = [];
      for (const log of logs) {
        const todo = await DB.getRepository(Todo).findOneBy({
          writer,
          id: log.todoId,
        });
        if (todo) result[date].todos.push(todo);
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
    req: Request<
      { email: string; date: string },
      {},
      {},
      { completed: boolean }
    >,
    res: Response<{ todos: Todo[]; logs: TodoLog[] }>
  ) => {
    const { email: writer, date } = req.params;
    const { completed } = req.query;

    const todos = await DB.getRepository(Todo).findBy({
      writer: writer,
    });

    const result: { todos: Todo[]; logs: TodoLog[] } = { todos: [], logs: [] };
    for (const todo of todos) {
      const log = await DB.getRepository(TodoLog).findOneBy({
        todoId: todo.id,
        date: date,
        completed,
      });
      if (log) {
        result.todos.push(todo);
        result.logs.push(log);
      }
    }

    return res.json(result);
  }
);

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
    console.log("[debug] HERE");
    console.log(folder, content, dates, days);

    // todo 데이터를 업데이트 한다.
    const result: any[] = [
      await DB.getRepository(Todo).update(id, {
        folder,
        content,
        days: JSON.stringify(days),
      }),
    ];

    // 만약 dates가 있다면 todo-log를 전부 삭제하고, 다시 추가한다. 단, 완료된 일은 삭제하지 않는다.
    // 기존에 있던 것을 비교하여 삭제, 추가보다 전부 삭제, 추가가 더 효율적으로 생각하여 이 방식을 택한다.
    console.log(dates);
    if (dates && dates.length) {
      result.push(
        await DB.getRepository(TodoLog).delete({
          todoId: id,
          completed: false,
        })
      );
      for (const date of dates) {
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
  }
);

// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
// 그리고 todo-logs에 접근하여 해당하는 log를 모두 삭제한다.
router.delete(
  "/",
  async (req: Request<{}, {}, { id: number }>, res: Response) => {
    const id = req.body.id;

    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다");
    }

    const result = await DB.getRepository(Todo).update(id, {
      deleted: true,
    });

    return res.json(result);
  }
);
