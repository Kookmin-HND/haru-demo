import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Generated,
  UpdateDateColumn,
} from "typeorm";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class Post {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  writer: string;

  @Column()
  content: string;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updated_at: Date;
}
