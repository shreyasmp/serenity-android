package us.nineworlds.serenity.emby.server.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import us.nineworlds.serenity.emby.server.model.AuthenticationResult

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27))
class EmbyAPIClientTest {

  lateinit var client: EmbyAPIClient

  @Before
  fun setUp() {
    client = EmbyAPIClient()
    client.updateBaseUrl("http://plexserver:8096")
  }

  @Test
  fun retrieveAllPublicUsers() {
    val result = client.fetchAllPublicUsers()
    assertThat(result).isNotEmpty.hasSize(2)
  }

  @Test fun loginAdminUser() {
    val authenticateResult = authenticate()

    assertThat(authenticateResult).isNotNull()
    assertThat(authenticateResult.accesToken).isNotBlank()
    assertThat(client.serverId).isNotBlank()
    assertThat(client.accessToken).isNotBlank()
    assertThat(client.userId).isNotBlank()
  }

  @Test fun testCurrentUsersViews() {
    val authenticate = authenticate()

    val result = client.currentUserViews()
    assertThat(result.items).isNotEmpty
  }

  @Test fun availableFiltersForCurrentUser() {
    authenticate()

    val currentViews = client.currentUserViews()
    val id = currentViews.items[2].id
    val result = client.filters(itemId = id)
    assertThat(result).isNotNull()
  }

  @Test fun generateMainMenuForCurrentUser() {
    authenticate()

    val result = client.retrieveRootData()
    assertThat(result).isNotNull
    assertThat(result!!.directories).isNotEmpty
  }

  fun authenticate(): AuthenticationResult {
    val users = client.fetchAllPublicUsers()
    val user = users[0]

    val authenticateResult = client.authenticate(user.name!!, "")
    return authenticateResult
  }
}