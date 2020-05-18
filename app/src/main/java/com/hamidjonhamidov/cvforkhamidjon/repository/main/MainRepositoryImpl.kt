package com.hamidjonhamidov.cvforkhamidjon.repository.main

import android.util.Log
import com.hamidjonhamidov.cvforkhamidjon.data_requests.api.main.MainApiService
import com.hamidjonhamidov.cvforkhamidjon.data_requests.persistence.AppDatabase
import com.hamidjonhamidov.cvforkhamidjon.di.main_subcomponent.MainActivityScope
import com.hamidjonhamidov.cvforkhamidjon.models.api.main.*
import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.AboutMeModel
import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.AchievementModel
import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.ProjectModel
import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.SkillModel
import com.hamidjonhamidov.cvforkhamidjon.repository.NetworkApiCall
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.state.MainStateEvent
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.state.MainViewState
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.state.MainViewState.*
import com.hamidjonhamidov.cvforkhamidjon.util.ApiResponseHandler
import com.hamidjonhamidov.cvforkhamidjon.util.ApiResult
import com.hamidjonhamidov.cvforkhamidjon.util.DataState
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants.MESSAGE_NETWORK_ERROR_CACHE_SUCCESS
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants.MESSAGE_NETWORK_NOT_ALLOWED_CACHE_SUCCESS
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants.MESSAGE_NETWORK_SUCCESS_CACHE_SUCCESSS
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants.MESSAGE_NO_INTERNET_CACHE_SUCCESS
import com.hamidjonhamidov.cvforkhamidjon.util.constants.NetworkConstants.MESSSAGE_NETWORK_TIMEOUT_CACHE_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@MainActivityScope
class MainRepositoryImpl
@Inject
constructor(
    val apiService: MainApiService,
    val appDatabase: AppDatabase
) : MainRepository, NetworkApiCall() {

    private val TAG = "AppDebug"

    override fun getAboutMe(
        stateEvent: MainStateEvent,
        isNetworkAvailable: Boolean,
        isNetworkAllowed: Boolean
    ): Flow<DataState<MainViewState, MainStateEvent>> = flow {

        // set network response to network error no internet available as a default
        var response: ApiResult<List<AboutMeRemoteModel>?> =
            ApiResult.GenericError(
                null,
                NetworkConstants.NETWORK_ERROR_NO_INTERNET
            )

        // if network is available and allowed, try to get from remote
        if (isNetworkAvailable && isNetworkAllowed)
            response =
                safeApiCall(Dispatchers.IO) { apiService.getAboutMeSync() }

        // set cache response to default empty
        var cacheRepsonse: List<AboutMeModel>? =
            listOf()

        // if there has been some error from internet or internet not allowed, try to receive it from cache
        if (response is ApiResult.GenericError || !isNetworkAllowed) {
            cacheRepsonse = withContext(Dispatchers.IO) { appDatabase.getAboutMeDao().getAboutMe() }
        }

        // process cache and remote response
        val result = withContext(Dispatchers.Default) {
            object :
                ApiResponseHandler<MainViewState, MainStateEvent, AboutMeRemoteModel, AboutMeModel>(
                    response,
                    stateEvent,
                    cacheRepsonse,
                    isNetworkAllowed
                ) {
                override fun handleNetworkSuccessCacheSuccess(
                    stateEvent: MainStateEvent,
                    remoteResponse: List<AboutMeRemoteModel>
                ): DataState<MainViewState, MainStateEvent> {
                    val aboutMeModel = remoteResponse[0].convertToAboutMeModel()
                    GlobalScope.launch((Dispatchers.IO)) {
                        appDatabase.getAboutMeDao().replaceAboutMe(aboutMeModel)
                    }
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            homeFragmentView = HomeFragmentView(aboutMeModel)
                        ),
                        message = MESSAGE_NETWORK_SUCCESS_CACHE_SUCCESSS.copy()
                    )
                }

                override fun handleNetworkTimeoutCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AboutMeModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            homeFragmentView = HomeFragmentView(cacheResponseObject[0])
                        ),
                        message = MESSSAGE_NETWORK_TIMEOUT_CACHE_SUCCESS
                    )
                }

                override fun handleNoInternetCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AboutMeModel>
                ): DataState<MainViewState, MainStateEvent> {

                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            homeFragmentView = HomeFragmentView(cacheResponseObject[0])
                        ),
                        message = MESSAGE_NO_INTERNET_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkFailureCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AboutMeModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            homeFragmentView = HomeFragmentView(cacheResponseObject[0])
                        ),
                        message = MESSAGE_NETWORK_ERROR_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkNotAllowedCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AboutMeModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            homeFragmentView = HomeFragmentView(cacheResponseObject[0])
                        ),
                        message = MESSAGE_NETWORK_NOT_ALLOWED_CACHE_SUCCESS
                    )
                }
            }.result
        }

        emit(result)

    }

    override fun getMySkills(
        stateEvent: MainStateEvent,
        isNetworkAvailable: Boolean,
        isNetworkAllowed: Boolean
    ): Flow<DataState<MainViewState, MainStateEvent>> = flow {

        // set remote response to network error no internet available as default
        var response: ApiResult<List<SkillRemoteModel>?> =
            ApiResult.GenericError(
                null,
                NetworkConstants.NETWORK_ERROR_NO_INTERNET
            )

        // if internet is available request from internet
        if (isNetworkAvailable)
            response =
                safeApiCall(Dispatchers.IO) { apiService.getSkillsSync() }

        // set cache response to empty list as default
        var cacheRepsonse: List<SkillModel>? =
            listOf()

        // if there has been some error from internet try to receive it from cache
        if (response is ApiResult.GenericError) {
            cacheRepsonse = withContext(Dispatchers.IO) { appDatabase.getSkillsDao().getSkills() }
        }

        val result = withContext(Dispatchers.Default) {
            object :
                ApiResponseHandler<MainViewState, MainStateEvent, SkillRemoteModel, SkillModel>(
                    response,
                    stateEvent,
                    cacheRepsonse,
                    isNetworkAllowed
                ) {
                override fun handleNetworkSuccessCacheSuccess(
                    stateEvent: MainStateEvent,
                    remoteResponse: List<SkillRemoteModel>
                ): DataState<MainViewState, MainStateEvent> {
                    val skillsList = remoteResponse.map { it.convertToSkillModel() }

                    GlobalScope.launch((Dispatchers.IO)) {
                        appDatabase.getSkillsDao().insertManyAndReplace(skillsList)
                    }
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            mySkillsFragmentView = MySkillsFragmentView(skillsList)
                        ),
                        message = MESSAGE_NETWORK_SUCCESS_CACHE_SUCCESSS.copy()
                    )
                }

                override fun handleNetworkTimeoutCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<SkillModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            mySkillsFragmentView = MySkillsFragmentView(cacheResponseObject)
                        ),
                        message = MESSSAGE_NETWORK_TIMEOUT_CACHE_SUCCESS
                    )
                }

                override fun handleNoInternetCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<SkillModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            mySkillsFragmentView = MySkillsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NO_INTERNET_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkFailureCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<SkillModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            mySkillsFragmentView = MySkillsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_ERROR_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkNotAllowedCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<SkillModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            mySkillsFragmentView = MySkillsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_NOT_ALLOWED_CACHE_SUCCESS
                    )
                }
            }.result
        }

        emit(result)

    }

    override fun getAchievements(
        stateEvent: MainStateEvent,
        isNetworkAvailable: Boolean,
        isNetworkAllowed: Boolean
    ): Flow<DataState<MainViewState, MainStateEvent>> = flow {

        // set remote response to network error no internet available as default
        var response: ApiResult<List<AchievementRemoteModel>?> =
            ApiResult.GenericError(
                null,
                NetworkConstants.NETWORK_ERROR_NO_INTERNET
            )


        // if internet is available, request from internet
        if (isNetworkAvailable)
            response =
                safeApiCall(Dispatchers.IO) { apiService.getAchievementsSync() }


        // set cache response to empty list as default
        var cacheRepsonse: List<AchievementModel>? =
            listOf()

        // if there has been some error from internet try to receive it from cache
        if (response is ApiResult.GenericError) {
            cacheRepsonse = withContext(Dispatchers.IO) {
                appDatabase.getAchievementsDao().getAllAchievements()
            }
        }

        val result = withContext(Dispatchers.Default) {
            object :
                ApiResponseHandler<MainViewState, MainStateEvent, AchievementRemoteModel, AchievementModel>(
                    response,
                    stateEvent,
                    cacheRepsonse,
                    isNetworkAllowed
                ) {
                override fun handleNetworkSuccessCacheSuccess(
                    stateEvent: MainStateEvent,
                    remoteResponse: List<AchievementRemoteModel>
                ): DataState<MainViewState, MainStateEvent> {
                    val achievementList = remoteResponse.map { it.convertToAchievmentModel() }

                    GlobalScope.launch((Dispatchers.IO)) {
                        appDatabase.getAchievementsDao().insertManyAndReplace(achievementList)
                    }
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            achievementsFragmentView = AchievementsFragmentView(achievementList)
                        ),
                        message = MESSAGE_NETWORK_SUCCESS_CACHE_SUCCESSS.copy()
                    )
                }

                override fun handleNetworkTimeoutCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AchievementModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            achievementsFragmentView = AchievementsFragmentView(cacheResponseObject)
                        ),
                        message = MESSSAGE_NETWORK_TIMEOUT_CACHE_SUCCESS
                    )
                }

                override fun handleNoInternetCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AchievementModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            achievementsFragmentView = AchievementsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NO_INTERNET_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkFailureCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AchievementModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            achievementsFragmentView = AchievementsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_ERROR_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkNotAllowedCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<AchievementModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            achievementsFragmentView = AchievementsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_NOT_ALLOWED_CACHE_SUCCESS
                    )
                }
            }.result
        }

        emit(result)

    }

    override fun getProjects(
        stateEvent: MainStateEvent,
        isNetworkAvailable: Boolean,
        isNetworkAllowed: Boolean
    ): Flow<DataState<MainViewState, MainStateEvent>> = flow {

        // set remote response to network error no internet available as default
        var response: ApiResult<List<ProjectsRemoteModel>?> =
            ApiResult.GenericError(
                null,
                NetworkConstants.NETWORK_ERROR_NO_INTERNET
            )

        // if internet is available, request from internet
        if (isNetworkAvailable)
            response =
                safeApiCall(Dispatchers.IO) { apiService.getProjectsSync() }

        // set cache response to empty list as default
        var cacheRepsonse: List<ProjectModel>? =
            listOf()

        // if there has been some error from internet try to receive it from cache
        if (response is ApiResult.GenericError) {
            cacheRepsonse =
                withContext(Dispatchers.IO) { appDatabase.getProjectsDao().getAllProjects() }
        }

        val result = withContext(Dispatchers.Default) {
            object :
                ApiResponseHandler<MainViewState, MainStateEvent, ProjectsRemoteModel, ProjectModel>(
                    response,
                    stateEvent,
                    cacheRepsonse,
                    isNetworkAllowed
                ) {
                override fun handleNetworkSuccessCacheSuccess(
                    stateEvent: MainStateEvent,
                    remoteResponse: List<ProjectsRemoteModel>
                ): DataState<MainViewState, MainStateEvent> {
                    val projectList = remoteResponse.map { it.convertToProjectModel() }

                    GlobalScope.launch((Dispatchers.IO)) {
                        appDatabase.getProjectsDao().insertManyAndReplace(projectList)
                    }
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            projectsFragmentView = ProjectsFragmentView(projectList)
                        ),
                        message = MESSAGE_NETWORK_SUCCESS_CACHE_SUCCESSS.copy()
                    )
                }

                override fun handleNetworkTimeoutCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<ProjectModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            projectsFragmentView = ProjectsFragmentView(cacheResponseObject)
                        ),
                        message = MESSSAGE_NETWORK_TIMEOUT_CACHE_SUCCESS
                    )
                }

                override fun handleNoInternetCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<ProjectModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            projectsFragmentView = ProjectsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NO_INTERNET_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkFailureCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<ProjectModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            projectsFragmentView = ProjectsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_ERROR_CACHE_SUCCESS
                    )
                }

                override fun handleNetworkNotAllowedCacheSuccess(
                    stateEvent: MainStateEvent,
                    cacheResponseObject: List<ProjectModel>
                ): DataState<MainViewState, MainStateEvent> {
                    return DataState(
                        stateEvent = stateEvent,
                        viewState = MainViewState(
                            projectsFragmentView = ProjectsFragmentView(cacheResponseObject)
                        ),
                        message = MESSAGE_NETWORK_NOT_ALLOWED_CACHE_SUCCESS
                    )
                }
            }.result
        }

        emit(result)
    }

}























