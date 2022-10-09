import { Entity, Column, PrimaryGeneratedColumn } from "typeorm";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class User {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  lastName: string;

  @Column()
  nickName: string;

  @Column()
  todoId: number;
}
