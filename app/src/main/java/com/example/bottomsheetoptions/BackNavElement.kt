package com.example.bottomsheetoptions

/**
 * This class helps to unitize back navigation. There can be several folded BackNavElements.
 *
 * Each time user presses the Back button, the whole chain is asked one-by-one starting from the most child node
 * whether any handler needs to process itself. In case it needs - it processes and returns the Result.CANNOT_GO_BACK.
 * Whole chain do not proceed if ANY of the elements returns the Result.CANNOT_GO_BACK.
 *
 * So, the chain is processed only if ALL of its sub handles returned the Result.CAN_GO_BACK.
 *
 * This utilize the cases when user has several dialogs on the screen that needs to be closed
 * one-by-one.
 */
class BackNavElement private constructor(
    private var child: BackNavElement? = null,
    private val handler: () -> Result
) {

    enum class Result {
        CANNOT_GO_BACK,
        CAN_GO_BACK
    }

    /**
     * Adds element to the END of the chain.
     */
    fun add(element: BackNavElement?) {
        this.child?.let {
            it.add(element)
        } ?: run {
            this.child = element
        }
    }

    fun tryGoBack(): Result {
        if (child?.tryGoBack() == Result.CANNOT_GO_BACK) {
            return Result.CANNOT_GO_BACK
        }
        return handler()
    }

    companion object {

        fun default(child: BackNavElement? = null, handler: () -> Unit) =
            BackNavElement(
                child = child,
                handler = {
                    handler()
                    BackNavElement.Result.CAN_GO_BACK
                })

        fun needsProcessing(child: BackNavElement? = null, handler: () -> Result) =
            BackNavElement(child = child, handler = handler)
    }
}