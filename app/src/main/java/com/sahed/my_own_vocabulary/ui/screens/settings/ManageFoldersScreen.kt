package com.sahed.my_own_vocabulary.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sahed.my_own_vocabulary.data.local.entity.MainFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubFolderEntity
import com.sahed.my_own_vocabulary.data.local.entity.SubSubFolderEntity
import com.sahed.my_own_vocabulary.ui.components.MainFolderLanguageDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldAlertDialog
import com.sahed.my_own_vocabulary.ui.designsystem.components.EmeraldGlassCard
import com.sahed.my_own_vocabulary.ui.designsystem.components.bouncyClickable
import com.sahed.my_own_vocabulary.ui.designsystem.components.gentleEntrance
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldPalette
import com.sahed.my_own_vocabulary.ui.designsystem.theme.EmeraldTheme
import com.sahed.my_own_vocabulary.util.LanguageRegistry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFoldersScreen(
    viewModel: ManageFoldersViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val foldersWithHierarchy by viewModel.foldersWithHierarchy.collectAsStateWithLifecycle()

    var showAddMainDialog by remember { mutableStateOf(false) }
    var editingMainFolder by remember { mutableStateOf<MainFolderEntity?>(null) }
    var deleteMainConfirm by remember { mutableStateOf<MainFolderEntity?>(null) }

    var targetMainForSub by remember { mutableStateOf<MainFolderEntity?>(null) }
    var editingSubFolder by remember { mutableStateOf<SubFolderEntity?>(null) }
    var deleteSubConfirm by remember { mutableStateOf<SubFolderEntity?>(null) }

    var targetSubForSubSub by remember { mutableStateOf<SubFolderEntity?>(null) }
    var editingSubSubFolder by remember { mutableStateOf<SubSubFolderEntity?>(null) }
    var deleteSubSubConfirm by remember { mutableStateOf<SubSubFolderEntity?>(null) }

    var inputMainName by remember { mutableStateOf("") }
    var inputSourceLang by remember { mutableStateOf("de") }
    var inputTargetLang by remember { mutableStateOf("en") }

    var inputSubName by remember { mutableStateOf("") }
    var inputSubSubName by remember { mutableStateOf("") }

    // Dialog: Add Sub-Sub Folder
    targetSubForSubSub?.let { parentSub ->
        EmeraldAlertDialog(
            onDismissRequest = {
                targetSubForSubSub = null
                inputSubSubName = ""
            },
            title = "Add Sub-Sub Folder in ${parentSub.name}",
            content = {
                OutlinedTextField(
                    value = inputSubSubName,
                    onValueChange = { inputSubSubName = it },
                    label = { Text("Sub-Sub Folder Name") },
                    placeholder = { Text("e.g. Unit 1, Vocabulary List") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Create",
            onConfirm = {
                if (inputSubSubName.isNotBlank()) {
                    viewModel.addSubSubFolder(parentSub.id, inputSubSubName)
                    inputSubSubName = ""
                    targetSubForSubSub = null
                }
            }
        )
    }

    // Dialog: Edit Sub-Sub Folder
    editingSubSubFolder?.let { subSub ->
        var editSubSubName by remember(subSub) { mutableStateOf(subSub.name) }
        EmeraldAlertDialog(
            onDismissRequest = { editingSubSubFolder = null },
            title = "Edit Sub-Sub Folder",
            content = {
                OutlinedTextField(
                    value = editSubSubName,
                    onValueChange = { editSubSubName = it },
                    label = { Text("Folder Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Save",
            onConfirm = {
                if (editSubSubName.isNotBlank()) {
                    viewModel.updateSubSubFolder(subSub, editSubSubName)
                    editingSubSubFolder = null
                }
            }
        )
    }

    // Dialog: Delete Sub-Sub Folder Confirmation
    deleteSubSubConfirm?.let { subSub ->
        EmeraldAlertDialog(
            onDismissRequest = { deleteSubSubConfirm = null },
            title = "Delete Sub-Sub Folder",
            content = {
                Text(
                    text = "Are you sure you want to delete \"${subSub.name}\"?",
                    color = EmeraldTheme.extended.subText
                )
            },
            confirmButtonText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteSubSubFolder(subSub)
                deleteSubSubConfirm = null
            }
        )
    }

    // Dialog: Add Main Folder with Language Flag Picker & Search
    if (showAddMainDialog) {
        MainFolderLanguageDialog(
            onDismissRequest = { showAddMainDialog = false },
            ttsManager = viewModel.ttsManager,
            onConfirm = { name, sourceLang, targetLang ->
                viewModel.addMainFolder(name, sourceLang, targetLang)
                showAddMainDialog = false
            }
        )
    }

    // Dialog: Edit Main Folder with Language Flag Picker & Search
    editingMainFolder?.let { folder ->
        MainFolderLanguageDialog(
            onDismissRequest = { editingMainFolder = null },
            initialName = folder.name,
            initialSourceLang = folder.sourceLanguage,
            initialTargetLang = folder.targetLanguage,
            isEditing = true,
            ttsManager = viewModel.ttsManager,
            onConfirm = { name, sourceLang, targetLang ->
                viewModel.updateMainFolder(folder, name, sourceLang, targetLang)
                editingMainFolder = null
            }
        )
    }

    // Dialog: Delete Main Folder Confirmation
    deleteMainConfirm?.let { folder ->
        EmeraldAlertDialog(
            onDismissRequest = { deleteMainConfirm = null },
            title = "Delete Main Folder",
            content = {
                Text(
                    text = "Are you sure you want to delete \"${folder.name}\"? All sub-folders and vocabulary entries inside will also be deleted.",
                    color = EmeraldTheme.extended.subText
                )
            },
            confirmButtonText = "Delete Folder",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteMainFolder(folder)
                deleteMainConfirm = null
            }
        )
    }

    // Dialog: Add Sub-Folder
    targetMainForSub?.let { main ->
        EmeraldAlertDialog(
            onDismissRequest = { targetMainForSub = null },
            title = "Add Sub-Folder to ${main.name}",
            content = {
                OutlinedTextField(
                    value = inputSubName,
                    onValueChange = { inputSubName = it },
                    label = { Text("Sub-Folder Name") },
                    placeholder = { Text("e.g. Adjectives, Verbs, Food") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Add",
            onConfirm = {
                if (inputSubName.isNotBlank()) {
                    viewModel.addSubFolder(main.id, inputSubName)
                    inputSubName = ""
                    targetMainForSub = null
                }
            }
        )
    }

    // Dialog: Edit Sub-Folder
    editingSubFolder?.let { sub ->
        var editSubName by remember { mutableStateOf(sub.name) }
        EmeraldAlertDialog(
            onDismissRequest = { editingSubFolder = null },
            title = "Edit Sub-Folder",
            content = {
                OutlinedTextField(
                    value = editSubName,
                    onValueChange = { editSubName = it },
                    label = { Text("Sub-Folder Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButtonText = "Save",
            onConfirm = {
                if (editSubName.isNotBlank()) {
                    viewModel.updateSubFolder(sub, editSubName)
                    editingSubFolder = null
                }
            }
        )
    }

    // Dialog: Delete Sub-Folder Confirmation
    deleteSubConfirm?.let { sub ->
        EmeraldAlertDialog(
            onDismissRequest = { deleteSubConfirm = null },
            title = "Delete Sub-Folder",
            content = {
                Text(
                    text = "Are you sure you want to delete sub-folder \"${sub.name}\"?",
                    color = EmeraldTheme.extended.subText
                )
            },
            confirmButtonText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteSubFolder(sub)
                deleteSubConfirm = null
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Folders",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPalette.SoftEmerald)
                            .bouncyClickable { showAddMainDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Main Folder", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = foldersWithHierarchy,
                key = { _, item -> item.folder.id }
            ) { index, item ->
                Box(modifier = Modifier.gentleEntrance(index = index)) {
                    EmeraldGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Main Folder Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val flag = LanguageRegistry.getFlagForCode(item.folder.sourceLanguage)
                                    val langName = LanguageRegistry.findByCode(item.folder.sourceLanguage)?.name
                                        ?: item.folder.sourceLanguage.uppercase()
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = flag,
                                            fontSize = 22.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.folder.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "$langName (${item.folder.sourceLanguage.uppercase()}) ➔ ${item.folder.targetLanguage.uppercase()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldPalette.EmeraldGlow
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { targetMainForSub = item.folder },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Sub-folder",
                                            tint = EmeraldPalette.EmeraldGlow,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { editingMainFolder = item.folder },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = EmeraldTheme.extended.subText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { deleteMainConfirm = item.folder },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = EmeraldTheme.extended.subText.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Sub-folders list
                            if (item.subFolders.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(
                                    color = EmeraldTheme.extended.glassBorder.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (subItem in item.subFolders) {
                                        val sub = subItem.subFolder
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(EmeraldTheme.extended.surfaceTier2)
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Folder,
                                                        contentDescription = null,
                                                        tint = EmeraldTheme.extended.subText,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = sub.name,
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(EmeraldPalette.SoftEmerald.copy(alpha = 0.15f))
                                                            .bouncyClickable { targetSubForSubSub = sub }
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = Icons.Default.Add,
                                                                contentDescription = "Add Sub-Sub",
                                                                tint = EmeraldPalette.EmeraldGlow,
                                                                modifier = Modifier.size(12.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Text(
                                                                "Sub-Sub",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = EmeraldPalette.EmeraldGlow
                                                            )
                                                        }
                                                    }

                                                    IconButton(
                                                        onClick = { editingSubFolder = sub },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = "Edit",
                                                            tint = EmeraldTheme.extended.subText,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { deleteSubConfirm = sub },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Delete",
                                                            tint = EmeraldTheme.extended.subText.copy(alpha = 0.6f),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // Sub-Sub folders list
                                            if (subItem.subSubFolders.isNotEmpty()) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(start = 12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    for (subSub in subItem.subSubFolders) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(EmeraldTheme.extended.surfaceTier3)
                                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Rounded.Folder,
                                                                    contentDescription = null,
                                                                    tint = EmeraldPalette.EmeraldGlow.copy(alpha = 0.7f),
                                                                    modifier = Modifier.size(13.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(
                                                                    text = subSub.name,
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                            }

                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                IconButton(
                                                                    onClick = { editingSubSubFolder = subSub },
                                                                    modifier = Modifier.size(24.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Edit,
                                                                        contentDescription = "Edit Sub-Sub",
                                                                        tint = EmeraldTheme.extended.subText,
                                                                        modifier = Modifier.size(12.dp)
                                                                    )
                                                                }
                                                                IconButton(
                                                                    onClick = { deleteSubSubConfirm = subSub },
                                                                    modifier = Modifier.size(24.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Delete,
                                                                        contentDescription = "Delete Sub-Sub",
                                                                        tint = EmeraldTheme.extended.subText.copy(alpha = 0.6f),
                                                                        modifier = Modifier.size(12.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
