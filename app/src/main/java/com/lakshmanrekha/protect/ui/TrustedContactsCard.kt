package com.lakshmanrekha.protect.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lakshmanrekha.protect.utils.AppState

@Composable
fun TrustedContactsCard(
    onManageContacts: () -> Unit
) {
    val contacts = AppState.trustedContacts.toList()

    Card(
        onClick = onManageContacts,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* -------- LEFT ICON (LIKE COACH CARD) -------- */
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.People,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            /* -------- TEXT CONTENT -------- */
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Trusted Contacts",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(4.dp))

                when {
                    contacts.isEmpty() -> {
                        Text(
                            text = "No trusted contacts added",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    else -> {
                        val preview =
                            contacts.take(2).joinToString(", ")

                        Text(
                            text = if (contacts.size > 2)
                                "$preview +${contacts.size - 2} more"
                            else
                                preview,
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            /* -------- CHEVRON (LIKE COACH CARD) -------- */
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}