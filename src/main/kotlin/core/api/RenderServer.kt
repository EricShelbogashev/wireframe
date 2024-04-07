package api

import java.awt.image.BufferedImage

typealias OnUpdateListener = (BufferedImage) -> Unit
//typealias OnUpdateProgressListener = (Int, Int, BufferedImage) -> Unit

interface RenderServer {
//    /**
//     * Подписывается на обновления сцены.
//     *
//     * @param onUpdate слушатель обновлений сцены.
//     * @param scene сцена, на которую подписывается слушатель.
//     * @return контроллер для управления сценой.
//     *
//     * После вызова функции снаружи scene не должна модифицироваться, onUpdate не должен модифицироваться.
//     */
//    fun <T : SceneObject> subscribe(onUpdate: OnUpdateListener, scene: Scene<T>): Controller<Scene<T>>

//    /**
//     * Подписывается на обновления прогресса отрисовки сцены.
//     *
//     * @param onUpdate слушатель обновлений прогресса отрисовки сцены.
//     * @param scene сцена, на которую подписывается слушатель.
//     * @return контроллер для управления подпиской.
//     *
//     * После вызова функции снаружи scene не должна модифицироваться, onUpdate не должен модифицироваться.
//     */
//    fun <S : Scene> subscribe(onUpdate: OnUpdateProgressListener, scene: Scene): Controller<S>

    /**
     * Отписывается от слушателя обновлений сцены.
     *
     * @param listener слушатель, от которого нужно отписаться.
     */
    fun unsubscribe(listener: OnUpdateListener)

//    /**
//     * Отписывается от слушателя обновлений прогресса отрисовки сцены.
//     *
//     * @param listener слушатель, от которого нужно отписаться.
//     */
//    fun unsubscribe(listener: OnUpdateProgressListener)
}