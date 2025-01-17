package com.jerboa.ui.components.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.ui.components.common.CircularIcon

@Composable
fun CommunityName(community: CommunitySafe) {
    val displayName = community.title

    Text(
        text = displayName,
        color = MaterialTheme.colors.primary,
    )
}

@Preview
@Composable
fun CommunityNamePreview() {
    CommunityName(community = sampleCommunitySafe)
}

@Composable
fun CommunityLink(community: CommunitySafe) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        community.icon?.let {
            CircularIcon(icon = it)
        }
        CommunityName(community = community)
    }
}

@Preview
@Composable
fun CommunityLinkPreview() {
    CommunityLink(community = sampleCommunitySafe)
}
