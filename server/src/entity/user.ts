import { Entity, Column, PrimaryGeneratedColumn } from "typeorm";

// user 스키마 생성

@Entity()
export class User {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  email: string;

  @Column()
  name: string;

  @Column({ type: Date })
  createAt: string;
}
