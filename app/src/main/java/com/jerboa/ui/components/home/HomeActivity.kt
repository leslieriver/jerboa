package com.jerboa.ui.components.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.openLink
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostListingsViewModel

@Composable
fun HomeActivity(
    navController: NavController,
    postListingsViewModel: PostListingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    isScrolledToEnd: () -> Unit,
) {

    Log.d("jerboa", "got to home activity")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accounts = accounts)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Column {
                    HomeHeader(
                        scope, scaffoldState,
                        selectedSortType = postListingsViewModel.sortType,
                        selectedListingType = postListingsViewModel.listingType,
                        onClickSortType = { sortType ->
                            postListingsViewModel.fetchPosts(
                                auth = account?.jwt,
                                clear = true,
                                changeSortType = sortType,
                            )
                        },
                        onClickListingType = { listingType ->
                            postListingsViewModel.fetchPosts(
                                auth = account?.jwt,
                                clear = true,
                                changeListingType = listingType,
                            )
                        }
                    )
                    if (postListingsViewModel.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            drawerShape = MaterialTheme.shapes.small,
            drawerContent = {
                Drawer(
                    myPerson = siteViewModel.siteRes?.my_user?.local_user_view?.person,
                    accounts = accounts,
                    navController = navController,
                    onSwitchAccountClick = {
                        accountViewModel.removeCurrent()
                        accountViewModel.setCurrent(it.id)
                        API.changeLemmyInstance(it.instance)

                        // Refetch the site
                        siteViewModel.fetchSite(it.jwt)
                    },
                    onSignOutClick = {
                        accounts?.also { accounts ->
                            getCurrentAccount(accounts)?.also {
                                accountViewModel.delete(it)
                                val updatedList = accounts.toMutableList()
                                updatedList.remove(it)

                                if (updatedList.isNotEmpty()) {
                                    accountViewModel.setCurrent(updatedList[0].id)
                                }
                            }
                        }
                    }
                )
            },
            content = {
                PostListings(
                    posts = postListingsViewModel.posts,
                    onUpvoteClick = { postView ->
                        postListingsViewModel.likePost(
                            voteType = VoteType.Upvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onDownvoteClick = { postView ->
                        postListingsViewModel.likePost(
                            voteType = VoteType.Downvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onSaveClick = { postView ->
                        postListingsViewModel.savePost(
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onPostClick = { postView ->
                        navController.navigate("post/${postView.post.id}?fetch=true")
                    },
                    onPostLinkClick = { url ->
                        openLink(url, ctx)
                    },
                    onSwipeRefresh = {
                        postListingsViewModel.fetchPosts(
                            auth = account?.jwt,
                            clear = true,
                        )
                    },
                    loading = postListingsViewModel.loading &&
                        postListingsViewModel.page == 1 &&
                        postListingsViewModel.posts.isNotEmpty(),
                    isScrolledToEnd = isScrolledToEnd,
                )
            }
        )
    }
}
