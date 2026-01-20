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
    
    // Articles
    @GET("api/articles")
    suspend fun getAllArticles(): List<Article>
    
    @GET("api/articles/{id}")
    suspend fun getArticleById(@Path("id") id: Long): Article
    
    @GET("api/articles/employee/{employeeId}")
    suspend fun getArticlesByEmployee(@Path("employeeId") employeeId: Long): List<Article>
    
    @POST("api/articles")
    suspend fun createArticle(@Body request: ArticleCreateRequest): Article
    
    @PUT("api/articles/{id}")
    suspend fun updateArticle(@Path("id") id: Long, @Body request: ArticleCreateRequest): Article
    
    @DELETE("api/articles/{id}")
    suspend fun deleteArticle(@Path("id") id: Long): retrofit2.Response<Unit>
    
    // Research Teams
    @GET("api/research-teams")
    suspend fun getResearchTeams(): List<ResearchTeam>
    
    @GET("api/research-teams/{id}")
    suspend fun getResearchTeamById(@Path("id") id: Long): ResearchTeam
    
    @GET("api/research-teams/leader/{leaderId}")
    suspend fun getTeamsByLeader(@Path("leaderId") leaderId: Long): List<ResearchTeam>
    
    @POST("api/research-teams")
    suspend fun createResearchTeam(@Body request: ResearchTeamCreateRequest): ResearchTeam
    
    @PUT("api/research-teams/{id}")
    suspend fun updateResearchTeam(@Path("id") id: Long, @Body request: ResearchTeamCreateRequest): ResearchTeam
    
    @DELETE("api/research-teams/{id}")
    suspend fun deleteResearchTeam(@Path("id") id: Long): retrofit2.Response<Unit>
    
    // Team Members
    @GET("api/research-teams/{teamId}/members")
    suspend fun getTeamMembers(@Path("teamId") teamId: Long): List<TeamMember>
    
    @POST("api/research-teams/members/employee")
    suspend fun addEmployeeToTeam(@Body request: TeamMemberRequest): TeamMember
    
    @POST("api/research-teams/members/student")
    suspend fun addStudentToTeam(@Body request: TeamMemberRequest): TeamMember
    
    @DELETE("api/research-teams/members/{memberId}")
    suspend fun removeMember(@Path("memberId") memberId: Long): retrofit2.Response<Unit>
    
    // Research Works
    @GET("api/research-teams/{teamId}/works")
    suspend fun getTeamWorks(@Path("teamId") teamId: Long): List<TeamResearchWork>
    
    @POST("api/research-teams/works")
    suspend fun createWork(@Body request: TeamResearchWorkRequest): TeamResearchWork
    
    @PUT("api/research-teams/works/{id}")
    suspend fun updateWork(@Path("id") id: Long, @Body request: TeamResearchWorkRequest): TeamResearchWork
    
    @DELETE("api/research-teams/works/{id}")
    suspend fun deleteWork(@Path("id") id: Long): retrofit2.Response<Unit>
}
