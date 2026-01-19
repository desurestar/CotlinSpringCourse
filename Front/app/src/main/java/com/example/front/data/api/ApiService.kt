package com.example.front.data.api

import com.example.front.data.model.*
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): User
    
    @GET("api/auth/me")
    suspend fun getCurrentUser(): User
    
    // Departments
    @GET("api/departments")
    suspend fun getDepartments(@Query("search") search: String? = null): List<Department>
    
    @GET("api/departments/{id}")
    suspend fun getDepartmentById(@Path("id") id: Long): DepartmentDetail
    
    @POST("api/departments")
    suspend fun createDepartment(@Body request: DepartmentRequest): Department
    
    @PUT("api/departments/{id}")
    suspend fun updateDepartment(@Path("id") id: Long, @Body request: DepartmentRequest): Department
    
    @DELETE("api/departments/{id}")
    suspend fun deleteDepartment(@Path("id") id: Long)
    
    // Employees
    @GET("api/employees")
    suspend fun getEmployees(
        @Query("search") search: String? = null,
        @Query("departmentId") departmentId: Long? = null
    ): List<Employee>
    
    @GET("api/employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: Long): Employee
    
    @POST("api/employees")
    suspend fun createEmployee(@Body request: EmployeeCreateRequest): Employee
    
    @PUT("api/employees/{id}")
    suspend fun updateEmployee(@Path("id") id: Long, @Body request: EmployeeUpdateRequest): Employee
    
    @DELETE("api/employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Long)
    
    // Posts
    @GET("api/posts")
    suspend fun getPosts(): List<Post>
    
    // Groups
    @GET("api/groups")
    suspend fun getGroups(@Query("search") search: String? = null): List<Group>
    
    @GET("api/groups/{id}")
    suspend fun getGroupById(@Path("id") id: Long): GroupDetail
    
    // Students
    @GET("api/students")
    suspend fun getStudents(
        @Query("search") search: String? = null,
        @Query("groupId") groupId: Long? = null
    ): List<Student>
    
    @GET("api/students/{id}")
    suspend fun getStudentById(@Path("id") id: Long): Student
}
