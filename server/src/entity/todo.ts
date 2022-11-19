import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
} from "typeorm";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class Todo {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: false })
  writer: string;

  @Column({ nullable: false })
  folder: string;

  @Column({ nullable: false })
  content: string;

  @Column({ nullable: false })
  days: string; // boolean[]

  @Column({ nullable: false })
  deleted: boolean;

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
