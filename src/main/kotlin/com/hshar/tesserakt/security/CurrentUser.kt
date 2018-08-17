package com.hshar.tesserakt.security

import java.lang.annotation.ElementType

@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class CurrentUser {

}