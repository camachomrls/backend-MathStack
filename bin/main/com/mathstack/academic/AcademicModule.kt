package com.mathstack.academic

import com.mathstack.academic.application.CreateExerciseUseCase
import com.mathstack.academic.application.CreateLessonTypeUseCase
import com.mathstack.academic.application.CreateLessonUseCase
import com.mathstack.academic.application.CreateSubjectUseCase
import com.mathstack.academic.application.DeleteExerciseUseCase
import com.mathstack.academic.application.DeleteLessonUseCase
import com.mathstack.academic.application.GetExercisesByLessonUseCase
import com.mathstack.academic.application.GetLessonByIdUseCase
import com.mathstack.academic.application.GetLessonsBySubjectUseCase
import com.mathstack.academic.application.ListLessonTypesUseCase
import com.mathstack.academic.application.ListSubjectsUseCase
import com.mathstack.academic.application.UpdateLessonUseCase
import com.mathstack.academic.application.UpdateExerciseUseCase
import com.mathstack.academic.domain.repository.AcademicRepository
import com.mathstack.academic.infrastructure.persistence.PostgresAcademicRepository
import org.koin.dsl.module

val academicModule = module {
    single<AcademicRepository> { PostgresAcademicRepository() }
    single { CreateSubjectUseCase(get()) }
    single { ListSubjectsUseCase(get()) }
    single { CreateLessonTypeUseCase(get()) }
    single { ListLessonTypesUseCase(get()) }
    single { CreateLessonUseCase(get()) }
    single { GetLessonsBySubjectUseCase(get(), get()) }
    single { GetLessonByIdUseCase(get()) }
    single { CreateExerciseUseCase(get()) }
    single { GetExercisesByLessonUseCase(get()) }
    single { DeleteLessonUseCase(get()) }
    single { DeleteExerciseUseCase(get()) }
    single { UpdateLessonUseCase(get()) }
    single { UpdateExerciseUseCase(get()) }
}
