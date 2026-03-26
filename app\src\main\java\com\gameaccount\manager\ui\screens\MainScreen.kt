package com.gameaccount.manager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gameaccount.manager.ui.viewmodel.AccountViewModel
import com.gameaccount.manager.ui.components.AccountList
import com.gameaccount.manager.ui.components.AddAccountDialog
import com.gameaccount.manager.ui.components.RentDialog
import com.gameaccount.manager.ui.components.StatisticsCard
import com.gameaccount.manager.ui.components.RecycleBinDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AccountViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showRecycleBin by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("all") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("游戏账号出租管理系统") },
                actions = {
                    IconButton(onClick = { /* 主题设置 */ }) {
                        Icon(Icons.Default.Palette, contentDescription = "主题")
                    }
                    IconButton(onClick = { showRecycleBin = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "回收站")
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 统计卡片
            StatisticsCard(
                total = uiState.totalCount,
                available = uiState.availableCount,
                rented = uiState.rentedCount,
                maintenance = uiState.maintenanceCount
            )

            // 筛选按钮
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->
                    selectedFilter = filter
                    viewModel.setFilter(filter)
                }
            )

            // 账号列表
            AccountList(
                accounts = uiState.filteredAccounts,
                onRentClick = { account ->
                    viewModel.selectAccountForRent(account)
                },
                onReturnClick = { account ->
                    viewModel.returnAccount(account.id)
                },
                onEditClick = { account ->
                    viewModel.selectAccountForEdit(account)
                    showAddDialog = true
                },
                onDeleteClick = { account ->
                    viewModel.deleteAccount(account)
                },
                onCopyPhone = { phone ->
                    // 复制手机号
                }
            )
        }
    }

    // 添加/编辑账号对话框
    if (showAddDialog) {
        AddAccountDialog(
            account = uiState.editingAccount,
            onDismiss = {
                showAddDialog = false
                viewModel.clearEditing()
            },
            onConfirm = { account ->
                if (uiState.editingAccount != null) {
                    viewModel.updateAccount(account)
                } else {
                    viewModel.addAccount(account)
                }
                showAddDialog = false
            }
        )
    }

    // 出租对话框
    if (uiState.showRentDialog) {
        RentDialog(
            account = uiState.selectedAccountForRent,
            onDismiss = { viewModel.hideRentDialog() },
            onConfirm = { renterName, renterContact, duration, unit, needHeartService ->
                uiState.selectedAccountForRent?.let { account ->
                    viewModel.rentAccount(
                        account.id,
                        renterName,
                        renterContact,
                        duration,
                        unit,
                        needHeartService
                    )
                }
                viewModel.hideRentDialog()
            }
        )
    }

    // 回收站对话框
    if (showRecycleBin) {
        RecycleBinDialog(
            deletedAccounts = uiState.deletedAccounts,
            onDismiss = { showRecycleBin = false },
            onRestore = { account ->
                viewModel.restoreAccount(account.id)
            },
            onDeletePermanently = { account ->
                viewModel.deletePermanently(account.id)
            },
            onClearAll = {
                viewModel.clearRecycleBin()
            }
        )
    }
}

@Composable
fun FilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "all" to "全部游戏",
        "光·遇" to "光·遇",
        "英雄联盟" to "英雄联盟",
        "王者荣耀" to "王者荣耀",
        "原神" to "原神"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, label) ->
            FilterChip(
                selected = selectedFilter == key,
                onClick = { onFilterSelected(key) },
                label = { Text(label) }
            )
        }
    }
}
