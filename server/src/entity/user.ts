import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  OneToMany,
  JoinTable,
} from "typeorm";
import { ProfileImageFile } from "./profileImageFile";

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


  //좋아요와 일대다 연결, 댓글이 삭제되면 좋아요도 삭제되도록 cascade
  @OneToMany(() => ProfileImageFile, (image) => image.user, {
    cascade: true,
  })
  @JoinTable()
  images: ProfileImageFile[];
}
