package com.hezy.model.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Defines application business logic using [kotlinx.coroutines.flow.Flow] as the main reactive component.
 * Please refer to [kotlinx.coroutines.flow.Flow] documentation to understand the ins and outs of this class.
 *
 * This class can be used in place of any other RxJava based UseCases.
 *
 * There is no subscriber class for this class and only inline functions shall be used to collect
 * and handle errors.
 */
abstract class FlowUseCase<Output> protected constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    internal var parentJob = SupervisorJob()

    /**
     * Method called when [FlowUseCase.execute] is called. It prepares the
     * [kotlinx.coroutines.flow.Flow] and interacts with domain components.
     *
     * @return an [kotlinx.coroutines.flow.Flow]
     */
    protected abstract fun setupFlow(): Flow<Output>

    /**
     * Executes the use case by:
     *
     *  - Preparing the chain of operations with [FlowUseCase.setupFlow]
     *  - Setting up the use case to be run in a background thread and to receive the result on the
     *  context associated with the scoped provided in the execute method. When run from a ViewModel,
     *  a viewModelScope should be provided. Hence the result would be on the Main Thread.
     *  - Binding the subscriber to it to propagate the result when it is available.
     *
     * Runs on Dispatcher.IO
     *
     * @param scope the coroutine scope in which the usecase should be launched (e.g. ViewModelScope)
     * @param onNext the value-function to provide the collected [Output].
     * @param onError the value-function to provide a [Throwable] in case of an error
     */
    fun execute(
        scope: CoroutineScope,
        onNext: suspend (value: Output) -> Unit,
        // onNext: FlowCollector<Output>,
        onError: (Throwable) -> Unit = {}
    ) {
        if (parentJob.isCompleted) {
            parentJob = SupervisorJob()
        }
        scope.launch(parentJob) {
            setupFlow()
                .flowOn(dispatcher)
                .onStart {
                    Logger.getLogger(javaClass.simpleName).log(Level.INFO, "executed")
                }
                .catch { onError(it) }
                .collect { onNext(it) }
        }
    }

    /**
     * Provides a way to chain the resulting [Flow] to some other operations
     *
     * DOES NOT RUN ON ANY PARTICULAR DISPATCHER.
     *
     * @return a [Flow]
     */
    fun chain(): Flow<Output> {
        return setupFlow()
    }

    fun clear() {
        parentJob.cancel()
    }
}
