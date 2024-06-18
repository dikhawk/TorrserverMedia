package com.dik.moduleinjector

interface ComponentHolder<A : BaseApi, B : BaseDependencies> {
    fun init(dependencies: B)
    fun get(): A
    fun reset()
}

interface BaseDependencies

interface BaseApi