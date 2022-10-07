package com.example.harudemo

import android.util.Log
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoInterface
import com.example.harudemo.todo.types.TodoDateInterface
import java.time.LocalDate

// 테스트하기 위한 임시 데이터 및 임시 유사 API 구현
object TodoDummyData {
    val todoInterfaces = arrayListOf<TodoInterface>(
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

    val todoDateInterfaces = arrayListOf<TodoDateInterface>(
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

    private var nextTodoId = 5

    fun getFolderTitles(): ArrayList<String> {
        val result = ArrayList<String>()
        for (todo in todoInterfaces) {
            if (todo.folder !in result) {
                result.add(todo.folder)
            }
        }
        return result
    }

    fun getFolderByFolderTitle(folderTitle: String): ArrayList<Section> {
        val result = ArrayList<Section>()
        result.add(Section(folderTitle, ArrayList()))
        for (todo in todoInterfaces) {
            if (todo.folder == folderTitle) {
                for (todoDate in todoDateInterfaces) {
                    if (todoDate.completed) {
                        continue
                    }
                    if (todoDate.todoId == todo.todoId) {
                        result[0].todoList.add(Todo(todo, todoDate))
                    }
                }
            }
        }
        return result
    }

    fun getAllSectionsByFolder(): ArrayList<Section> {
        val result = ArrayList<Section>()
        val folders = HashMap<String, ArrayList<Todo>>()

        for (todo in todoInterfaces) {
            if (todo.folder !in folders) {
                folders[todo.folder] = ArrayList<Todo>()
            }

            for (todoDate in todoDateInterfaces) {
                if (todoDate.completed) {
                    continue
                }
                if (todo.todoId == todoDate.todoId) {
                    folders[todo.folder]?.add(Todo(todo, todoDate))
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

        for (todo in todoInterfaces) {
            for (todoDate in todoDateInterfaces) {
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
                            dates[date.joinToString("-")]?.add(Todo(todo, todoDate))
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

        for (todo in todoInterfaces) {
            if (todo.folder !in folders) {
                folders[todo.folder] = ArrayList<Todo>()
            }

            for (todoDate in todoDateInterfaces) {
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
                    folders[todo.folder]?.add(Todo(todo, todoDate))
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

        for (todo in todoInterfaces) {
            for (todoDate in todoDateInterfaces) {
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
                            dates[date.joinToString("-")]?.add(Todo(todo, todoDate))
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

    fun addTodo(folder: String, content: String, date: String) {
        val tdInterface = TodoInterface(
            todoId = nextTodoId,
            writer = "tester@example.com",
            folder = folder,
            content = content,
            createdAt = LocalDate.now().toString(),
            begin = date,
            end = date,
        )

        val tdDateInterface = TodoDateInterface(
            nextTodoId,
            date = date,
            completed = false,
        )

        todoInterfaces.add(tdInterface)
        todoDateInterfaces.add(tdDateInterface)
        nextTodoId++
    }


    fun addTodo(folder: String, content: String, dates: ArrayList<String>) {
        dates.sortWith(Comparator { v1, v2 ->
            val date1 = v1.split('-').map { it.toInt() }
            val date2 = v2.split('-').map { it.toInt() }
            if (date1[0] == date2[0]) {
                if (date1[1] == date2[1]) {
                    return@Comparator date1[2].compareTo(date2[2])
                }
                return@Comparator date1[1].compareTo(date2[1])
            }
            return@Comparator date1[0].compareTo(date2[0])
        })

        val tdInterface = TodoInterface(
            todoId = nextTodoId,
            writer = "tester@example.com",
            folder = folder,
            content = content,
            createdAt = LocalDate.now().toString(),
            begin = dates[0],
            end = dates[dates.size - 1],
        )

        for (date in dates) {
            val tdDateInterface = TodoDateInterface(
                nextTodoId,
                date = date,
                completed = false,
            )
            todoDateInterfaces.add(tdDateInterface)
        }

        todoInterfaces.add(tdInterface)
        nextTodoId++
    }
}