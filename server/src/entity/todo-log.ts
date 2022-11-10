import {
  Column,
  CreateDateColumn,
  PrimaryColumn,
  UpdateDateColumn,
} from "typeorm";

// Todo가 완료되는 시점을 기록하는 데이터의 Entity
export class TodoLog {
  //id : auto Increment
  @PrimaryColumn()
  id: number;

  @Column({ nullable: false })
  todoId: number;

  @Column({ nullable: false })
  date: string;

  @CreateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
  })
  completedAt: Date;

  @UpdateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
    onUpdate: "CURRENT_TIMESTAMP(6)",
  })
  updatedAt: Date;
}
