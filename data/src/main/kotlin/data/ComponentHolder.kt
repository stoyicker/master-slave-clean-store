package data

import data.top.TopPostsFacadeComponent
import data.top.TopRequestSourceComponent

/**
 * A holder for dependency injectors.
 */
internal object ComponentHolder {
    lateinit var topPostsFacadeComponent: TopPostsFacadeComponent
    lateinit var topRequestSourceComponent: TopRequestSourceComponent
}
