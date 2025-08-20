package com.example.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class TaskApplication

fun main(args: Array<String>) {
	runApplication<TaskApplication>(*args)
}
