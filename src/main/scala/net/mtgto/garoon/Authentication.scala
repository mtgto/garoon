package net.mtgto.garoon

/**
 * 認証手段
 */
sealed trait Authentication

/**
 * 認証なし.
 *
 * ユーザー名とパスワードを引数に実行するセッション発行時などに利用する.
 */
object NoAuth extends Authentication

/**
 * ユーザー名とパスワードによる認証
 * @param username
 * @param password
 */
case class Password(username: String, password: String) extends Authentication

/**
 * セッションクッキーによる認証
 * @param cookie
 */
case class SessionCookie(cookie: Cookie) extends Authentication
