package com.example.harudemo

import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoInterface
import com.example.harudemo.todo.types.TodoDateInterface
import java.time.LocalDate

object TodoDummyData {
    val todoInterfaceTables = arrayListOf<TodoInterface>(
        TodoInterface(
            todoId = 1,
            writer = "tester@example.com",
            folder = "금융",
            content = "친구비 내기",
            createdAt = "2022-9-30",
            begin = "2022-9-30",
            end = "2022-10-5",
        ),
        TodoInterface(
            todoId = 2,
            writer = "tester@example.com",
            folder = "금융",
            content = "월세 내기",
            createdAt = "2022-9-30",
            begin = "2022-10-1",
            end = "2022-10-1",
        ),
        TodoInterface(
            todoId = 3,
            writer = "tester@example.com",
            folder = "과제",
            content = "모바일 프로그래밍",
            createdAt = "2022-9-30",
            begin = "2022-10-3",
            end = "2022-10-3",
        ),
        TodoInterface(
            todoId = 4,
            writer = "tester@example.com",
            folder = "개인",
            content = "사이드 프로젝트 하기",
            createdAt = "2022-9-30",
            begin = "2022-10-2",
            end = "2022-10-2",
        )

    )

    val todoDateInterfaceTables = arrayListOf<TodoDateInterface>(
        TodoDateInterface(
            todoId = 1,
            date = "2022-9-30",
            completed = true,
        ),
        TodoDateInterface(
            todoId = 1,
            date = "2022-10-1",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 1,
            date = "2022-10-5",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 2,
            date = "2022-10-1",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 3,
            date = "2022-10-3",
            completed = false
        ),
        TodoDateInterface(
            todoId = 4,
            date = "2022-10-3",
            completed = true
        )
    )

    fun getFolderTitles(): ArrayList<String> {
        val result = ArrayList<String>()
        for (todo in todoInterfaceTables) {
            if (todo.folder !in result) {
                result.add(todo.folder)
            }
        }
        return result
    }

    fun getFolderByFolderTitle(folderTitle: String): ArrayList<Section> {
        val result = ArrayList<Section>()
        result.add(Section(folderTitle, ArrayList()))
        for (todo in todoInterfaceTables) {
            if (todo.folder == folderTitle) {
                for (todoDate in todoDateInterfaceTables) {
                    if (todoDate.completed) {
                        continue
                    }
                    if (todoDate.todoId == todo.todoId) {
                        result[0].todoList.add(Todo(todo.content, todoDate))
                    }
                }
            }
        }
        return result
    }

    fun getAllSectionsByFolder(): ArrayList<Section> {
        val result = ArrayList<Section>()
        val folders = HashMap<String, ArrayList<Todo>>()

        for (todo in todoInterfaceTables) {
            if (todo.folder !in folders) {
                folders[todo.folder] = ArrayList<Todo>()
            }

            for (todoDate in todoDateInterfaceTables) {
                if (todoDate.completed) {
                    continue
                }
                if (todo.todoId == todoDate.todoId) {
                    folders[todo.folder]?.add(Todo(todo.content, todoDate))
                }
            }
        }

        for (folder in folders) {
            if (folders[folder.key]?.size == 0) {
                continue
            }
            result.add(Section(folder.key, folder.value))
        }
        return result
    }

    fun getAllCompletedTodoByDate(): ArrayList<Section> {
        val today = LocalDate.now()
        val week = ArrayList<ArrayList<Int>>()

        for (i in 0..6) {
            val date = today.minusDays(i.toLong()).toString().split("-")
            week.add(arrayListOf(date[0].toInt(), date[1].toInt(), date[2].toInt()))
        }


        val result = ArrayList<Section>()
        val dates = HashMap<String, ArrayList<Todo>>()

        for (date in week) {
            dates[date.joinToString("-")] = ArrayList()
        }

        for (todo in todoInterfaceTables) {
            for (todoDate in todoDateInterfaceTables) {
                if (!todoDate.completed) {
                    continue
                }

                if (todo.todoId == todoDate.todoId) {
                    val splitDate = todoDate.date.split('-')
                    for (date in week) {
                        if (splitDate[0].toInt() == date[0] &&
                            splitDate[1].toInt() == date[1] &&
                            splitDate[2].toInt() == date[2]
                        ) {
                            dates[date.joinToString("-")]?.add(Todo(todo.content, todoDate))
                            break
                        }
                    }
                }
            }
        }

        for (date in dates) {
            if (dates[date.key]?.size == 0) {
                continue
            }
            result.add(Section(date.key, date.value))
        }
        return result
    }

    fun getTodayTodoByFolder(): ArrayList<Section> {
        val today = LocalDate.now().toString().split('-')

        val result = ArrayList<Section>()
        val folders = HashMap<String, ArrayList<Todo>>()

        for (todo in todoInterfaceTables) {
            if (todo.folder !in folders) {
                folders[todo.folder] = ArrayList<Todo>()
            }

            for (todoDate in todoDateInterfaceTables) {
                if (todoDate.completed) {
                    continue
                }



                if (todo.todoId == todoDate.todoId) {
                    val splitDate = todoDate.date.split('-')
                    if (splitDate[0].toInt() != today[0].toInt() ||
                        splitDate[1].toInt() != today[1].toInt() ||
                        splitDate[2].toInt() != today[2].toInt()
                    ) {
                        continue
                    }
                    folders[todo.folder]?.add(Todo(todo.content, todoDate))
                }
            }
        }

        for (folder in folders) {
            if (folders[folder.key]?.size == 0) {
                continue
            }
            result.add(Section(folder.key, folder.value))
        }
        return result
    }

    fun getWeekTodoByDate(): ArrayList<Section> {
        val today = LocalDate.now()
        val week = ArrayList<ArrayList<Int>>()

        for (i in 0..6) {
            val date = today.plusDays(i.toLong()).toString().split("-")
            week.add(arrayListOf(date[0].toInt(), date[1].toInt(), date[2].toInt()))
        }


        val result = ArrayList<Section>()
        val dates = HashMap<String, ArrayList<Todo>>()

        for (date in week) {
            dates[date.joinToString("-")] = ArrayList()
        }

        for (todo in todoInterfaceTables) {
            for (todoDate in todoDateInterfaceTables) {
                if (todoDate.completed) {
                    continue
                }

                if (todo.todoId == todoDate.todoId) {
                    val splitDate = todoDate.date.split('-')
                    for (date in week) {
                        if (splitDate[0].toInt() == date[0] &&
                            splitDate[1].toInt() == date[1] &&
                            splitDate[2].toInt() == date[2]
                        ) {
                            dates[date.joinToString("-")]?.add(Todo(todo.content, todoDate))
                            break
                        }
                    }
                }
            }
        }

        for (date in dates) {
            if (dates[date.key]?.size == 0) {
                continue
            }
            result.add(Section(date.key, date.value))
        }
        return result
    }
}