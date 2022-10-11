import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
} from "typeorm";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class TodoDate {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: false })
  writer: string;

  @Column({ nullable: false })
  todoId: number;

  @Column({ nullable: false })
  date: string;

  @Column({ nullable: false })
  completed: boolean;

  @CreateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
  })
  createdAt: Date;

  @UpdateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
    onUpdate: "CURRENT_TIMESTAMP(6)",
  })
  updatedAt: Date;
}
