package com.example.pe_prm.ui.home;
import androidx.appcompat.app.AppCompatDelegate;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pe_prm.R;
import com.example.pe_prm.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements ItemTableAdapter.OnItemClickListener , DialogManager.DialogCallback{
    private FloatingActionButton fabMain, fabAddItem, fabAddType,fabSwitchTheme;
    private DialogManager dialogManager;
    private boolean isFabMenuOpen = false;
    private FragmentHomeBinding binding;
    private RecyclerView itemTable;
    private TabLayout tabLayout;
    private List<Item> allItems;
    private ItemTableAdapter adapter;

    private void setupFab() {
        fabMain = binding.fabMain;
        fabAddItem = binding.fabAddItem;
        fabAddType = binding.fabAddType;
        fabSwitchTheme = binding.fabSwitchTheme;


        fabMain.setOnClickListener(v -> toggleFabMenu());
        fabAddItem.setOnClickListener(v -> {
            toggleFabMenu();
            dialogManager.showAddItemDialog(typeList);
        });
        fabAddType.setOnClickListener(v -> {
            toggleFabMenu();
            dialogManager.showAddTypeDialog();
        });
        fabSwitchTheme.setOnClickListener(v -> switchTheme());
    }
    // Implement the switchTheme method:
    private void switchTheme() {
        // Check current theme and switch
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Switch to light mode
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Switch to dark mode
        }
        updateThemeIcon(); // Update the icon after switching
    }

    // Add a method to update the icon based on the current theme:
    private void updateThemeIcon() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            fabSwitchTheme.setImageResource(R.drawable.baseline_dark_mode_24); // Dark mode icon
        } else {
            fabSwitchTheme.setImageResource(R.drawable.baseline_light_mode_24); // Light mode icon
        }
    }

    private void toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu();
        } else {
            openFabMenu();
        }
    }
    private void openFabMenu() {
        isFabMenuOpen = true;
        fabMain.animate().rotation(45f).setDuration(300).start();

        fabAddItem.show();
        fabAddType.show();
        fabSwitchTheme.show();

        fabAddItem.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_65))
                .setDuration(300)
                .start();
        binding.fabAddItemTooltip.animate() // Tooltip follows FAB
                .translationY(-getResources().getDimension(R.dimen.standard_70))
                .setDuration(300)
                .withStartAction(() -> binding.fabAddItemTooltip.setVisibility(View.VISIBLE))
                .start();

        fabAddType.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_130))
                .setDuration(300)
                .start();
        binding.fabAddTypeTooltip.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_135))
                .setDuration(300)
                .withStartAction(() -> binding.fabAddTypeTooltip.setVisibility(View.VISIBLE))
                .start();

        fabSwitchTheme.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_195))
                .setDuration(300)
                .start();
        binding.fabSwitchThemeTooltip.animate()
                .translationY(-getResources().getDimension(R.dimen.standard_200))
                .setDuration(300)
                .withStartAction(() -> binding.fabSwitchThemeTooltip.setVisibility(View.VISIBLE))
                .start();
    }

    private void closeFabMenu() {
        isFabMenuOpen = false;
        fabMain.animate().rotation(0f).setDuration(300).start();

        fabAddItem.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    fabAddItem.hide();
                    binding.fabAddItemTooltip.setVisibility(View.GONE);
                })
                .start();
        binding.fabAddItemTooltip.animate()
                .translationY(0f)
                .setDuration(300)
                .start();

        fabAddType.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    fabAddType.hide();
                    binding.fabAddTypeTooltip.setVisibility(View.GONE);
                })
                .start();
        binding.fabAddTypeTooltip.animate()
                .translationY(0f)
                .setDuration(300)
                .start();

        fabSwitchTheme.animate()
                .translationY(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    fabSwitchTheme.hide();
                    binding.fabSwitchThemeTooltip.setVisibility(View.GONE);
                })
                .start();
        binding.fabSwitchThemeTooltip.animate()
                .translationY(0f)
                .setDuration(300)
                .start();
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dialogManager = new DialogManager(requireContext(), this);
        setupFab();
        updateThemeIcon(); // Set initial theme icon

        itemTable = binding.itemTable;
        tabLayout = binding.tabLayout;

        // Setup TabLayout with custom tabs
        setupTabLayout();

        // Setup RecyclerView
        itemTable.setLayoutManager(new LinearLayoutManager(getContext()));
        allItems = generateDummyData(); // Method to generate dummy data
        adapter = new ItemTableAdapter(allItems, this);
        itemTable.setAdapter(adapter);

        // Setup tab listener
        setupTabListener();

        return root;
    }

    @Override
    public void onItemAdded(Item newItem) {
        allItems.add(0, newItem);
        adapter.updateData(allItems);
    }
    @Override
    public void onShowDeleteSuccessMessage() {
        Snackbar.make(binding.getRoot(), "Item deleted successfully", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.md_theme_error, null))
                .setTextColor(Color.WHITE)
                .show();
    }
    public void onDeleteItem(Item item) {
        // Xóa sinh viên khỏi danh sách allItems
        allItems.remove(item);

        // Cập nhật adapter với danh sách mới
        adapter.updateData(new ArrayList<>(allItems));
    }
    @Override
    public void onTypeAdded(Type type) {
        Type newType = new Type(type.getIdType(), type.getNameType()); // Use the correct property
        typeList.add(newType);
    }
    @Override
    public void onItemUpdated(Item updatedItem) {
        // Tìm và cập nhật item trong danh sách
        int index = -1;
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getId().equals(updatedItem.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            allItems.set(index, updatedItem);
            adapter.updateData(allItems);
        }
    }

    private void setupTabLayout() {
        TabLayout.Tab allTab = tabLayout.newTab();
        allTab.setCustomView(createTabView("All", null)); // Replace with your icon
        tabLayout.addTab(allTab);

        TabLayout.Tab typeTab = tabLayout.newTab();
        typeTab.setCustomView(createTabView("By Type", R.drawable.baseline_arrow_drop_down_24));
        tabLayout.addTab(typeTab);
    }

    private View createTabView(String text, Integer iconResId) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
        TextView tabText = view.findViewById(R.id.tab_text);
        ImageView tabIcon = view.findViewById(R.id.tab_icon);

        tabText.setText(text);

        // Kiểm tra xem iconResId có phải là null không
        if (iconResId != null) {
            tabIcon.setImageResource(iconResId);
            tabIcon.setVisibility(View.VISIBLE); // Hiển thị icon nếu có
        } else {
            tabIcon.setVisibility(View.GONE); // Ẩn icon nếu không có
        }

        return view;
    }


    private List<String> getUniqueTypes() {
        return typeList.stream()
                .map(type -> type.getNameType() + " - " + type.getIdType())
                .collect(Collectors.toList());
    }


    private void showTypeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Type");

        List<String> types = getUniqueTypes();
        String[] typeArray = types.toArray(new String[0]);

        builder.setItems(typeArray, (dialog, which) -> {
            String selectedTypeText = typeArray[which];
            String selectedTypeId = selectedTypeText.split(" - ")[1]; // Extract the type ID
            filterItemData(selectedTypeId); // Filter by type ID
        });

        builder.show();
    }


    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    filterItemData(null); // Show all item
                } else {
                    showTypeSelectionDialog();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    showTypeSelectionDialog();
                }
            }
        });
    }

    private void filterItemData(String type) {
        List<Item> filteredList;
        if (type == null) {
            filteredList = new ArrayList<>(allItems);
        } else {
            filteredList = allItems.stream()
                    .filter(item -> item.getType().equals(type))
                    .collect(Collectors.toList());
        }
        adapter.updateData(filteredList);
    }

    @Override
    public void onItemClick(Item item, View itemView) {
        showPopupMenu(item, itemView);
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(Item item, View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        popup.getMenuInflater().inflate(R.menu.student_actions_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_update) {
                String typeName = typeList.stream()
                        .filter(type -> type.getIdType().equals(item.getType()))
                        .findFirst()
                        .map(Type::getNameType)
                        .orElse("Unknown");
                dialogManager.showUpdateItemDialog(item, typeList, typeName);
                return true;
            } else if (itemId == R.id.action_delete) {
                dialogManager.showDeleteConfirmationDialog(item);
                return true;
            } else if (itemId == R.id.action_view_details) {
                String typeName = typeList.stream()
                        .filter(type -> type.getIdType().equals(item.getType()))
                        .findFirst()
                        .map(Type::getNameType)
                        .orElse("Unknown");
                dialogManager.showItemDetailsDialog(item, typeName);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private List<Type> typeList = new ArrayList<>(Arrays.asList(
            new Type("GAM", "Gaming"),
            new Type("MUS", "Music"),
            new Type("MOV", "Movie"),
            new Type("BOK", "Book"),
            new Type("SPT", "Sport")
    ));

    private List<Item> generateDummyData() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(
                "GAM001",
                "The Legend of Zelda",
                "Nintendo",
                "03/03/2017",
                "GAM",
                "ZELDA-BOTW",
                "GAM"
        ));
        items.add(new Item(
                "MUS001",
                "Thriller",
                "Michael Jackson",
                "30/11/1982",
                "MUS",
                "MJ-THR",
                "MUS"
        ));
        items.add(new Item(
                "MOV001",
                "The Shawshank Redemption",
                "Frank Darabont",
                "14/10/1994",
                "MOV",
                "TSR-1994",
                "MOV"
        ));
        items.add(new Item(
                "BOK001",
                "1984",
                "George Orwell",
                "08/06/1949",
                "BOK",
                "ORW-1984",
                "BOK"
        ));
        items.add(new Item(
                "SPT001",
                "FIFA World Cup",
                "FIFA",
                "20/11/2022",
                "SPT",
                "WC-2022",
                "SPT"
        ));

        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
