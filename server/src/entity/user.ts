import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
} from "typeorm";

// user 스키마 생성

@Entity()
export class User {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  email: string;

  @Column() // hashing된 비밀번호
  password: string;

  @Column()
  name: string;

  @CreateDateColumn()
  createAt: Date;


  
}
