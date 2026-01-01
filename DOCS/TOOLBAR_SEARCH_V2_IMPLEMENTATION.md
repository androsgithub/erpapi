# Toolbar & Search System - Version 2.0

## Overview

**IMPLEMENTED VERSION 2 REQUIREMENTS** ✅

Refactored the toolbar and search system from a complex multi-select bulk operations pattern to a simplified form-based single-record manipulation system with explicit mode states.

## Key Changes

### 1. **Toolbar Component** (V2 - Simplified)

**File**: `src/shared/components/ui/Toolbar.tsx`

#### Changes from V1:
- ❌ Removed dropdown menu with multiple actions
- ✅ Added 5 VISIBLE BUTTONS aligned horizontally (side-by-side)
- ✅ Changed to single-record selection instead of multi-select
- ✅ Added form mode tracking (create | view | edit)
- ✅ Integrated delete confirmation dialog
- ✅ Built-in search dialog opening

#### Visible Buttons:
```
[Search] [New] [Edit*] [Delete*] [Reports]
         
* Edit and Delete are enabled only when a record is selected
```

#### Features:
- **Search Button**: Opens SearchDialog to find and select a single record
- **New Button**: Creates new record (enters Create mode)
- **Edit Button**: Edits selected record (enters Edit mode) - *disabled without selection*
- **Delete Button**: Deletes selected record with confirmation - *disabled without selection*
- **Reports Button**: Opens reports view (optional)

#### Delete Confirmation:
- Modal dialog with record ID display
- Prevents accidental deletion
- Error handling with user feedback

**Props**:
```typescript
interface ToolbarProps {
  hasSelection: boolean;              // Whether a record is selected
  selectedRecord?: any;                // Currently selected record data
  pageMode?: PageMode;                 // 'list' | 'view' | 'create' | 'edit'
  onSearch?: (record: any) => void;   // Single record selection callback
  onNew?: () => void;                 // New button click
  onEdit?: () => void;                // Edit button click
  onDelete?: () => Promise<void>;     // Delete button click (async)
  onReports?: () => void;             // Reports button click
  onModeChange?: (mode: PageMode) => void; // Mode change callback
  searchConfig?: SearchConfig;        // Search dialog configuration
  allRecords?: any[];                 // Records for search
  isLoading?: boolean;                // Loading state
}
```

### 2. **Search Dialog** (V2 - Simplified)

**File**: `src/shared/components/ui/SearchDialog.tsx`

#### Changes from V1:
- ❌ Removed multi-field advanced filtering
- ❌ Removed pagination controls
- ❌ Removed checkboxes and multi-select
- ✅ Single field selector (dropdown)
- ✅ Single search value input
- ✅ Search button (also responds to Enter key)
- ✅ Simple clickable results list
- ✅ Single record selection

#### UI Structure:
```
+─────────────────────────────────────┐
│  Search {EntityName}                │
│                                     │
│  Field: [Dropdown of fields] ▼      │
│  Value: [Search input field]        │
│  [Search Button]                    │
│                                     │
│  Results:                           │
│  ┌─────────────────────────────┐   │
│  │ Field1: value1 Field2: value2 → │
│  │ Field1: value3 Field2: value4 → │
│  │ Field1: value5 Field2: value6 → │
│  └─────────────────────────────┘   │
│                                     │
│  [Close Button]                     │
└─────────────────────────────────────┘
```

#### Features:
- **Field Selection**: Choose which field to search in
- **Value Input**: Type search term with Ctrl+Enter or Enter key support
- **Real-time Filtering**: Results update as you type (optional, on demand with button click)
- **Clickable Results**: Click any result to select that record
- **Display Configuration**: `displayFields` option shows only relevant fields

**Props**:
```typescript
interface SearchDialogProps {
  open: boolean;                    // Dialog visibility
  onOpenChange: (open: boolean) => void; // Open/close callback
  config: SearchConfig;             // Configuration
  records: any[];                   // Records to search in
  isLoading?: boolean;              // Loading state
  onSelect?: (record: any) => void; // Single record selection callback
}
```

### 3. **Form Mode Context** (NEW)

**File**: `src/core/application/context/FormModeContext.tsx`

#### Purpose:
Centralized state management for form modes and record selection across the entire application.

#### Modes:
- **create**: Creating new record (no existing record data)
- **view**: Viewing record details (read-only)
- **edit**: Editing record (form fields enabled)

#### Context API:
```typescript
interface FormModeContextType {
  // State
  mode: FormMode;                      // Current mode
  selectedRecord: any | null;          // Currently selected record
  isLoading: boolean;                  // Loading state
  error: string | null;                // Error message

  // Actions
  enterCreate: () => void;              // Enter create mode
  enterView: (record: any) => void;     // Enter view mode with record
  enterEdit: (record: any) => void;     // Enter edit mode with record
  exitForm: () => void;                 // Exit form (return to view)
  updateRecord: (record: any) => void;  // Update selected record data
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  resetForm: () => void;                // Reset to initial state
}
```

#### Usage:
```typescript
// In component
const { mode, selectedRecord, enterCreate, enterView, enterEdit, exitForm } = useFormMode();

// Change mode
enterCreate();                    // Start creating new
enterView(record);               // View existing record
enterEdit(record);               // Edit existing record
exitForm();                       // Exit form
```

### 4. **Updated Usuarios List Page** (V2)

**File**: `src/features/usuarios/presentation/pages/UsuariosListPage.tsx`

#### Changes:
- ✅ Integrated new Toolbar with visible buttons
- ✅ Integrated FormModeContext for state management
- ✅ Simplified search configuration (1 field selector, 1 input)
- ✅ Single record selection instead of multi-select
- ✅ Mode-based UI rendering (list view or form view)
- ✅ Form placeholder for create/view/edit modes
- ✅ Proper data flow: Search → View → Edit → Save

#### Page Flow:
```
List View
    ↓
[Search] button → SearchDialog → Select record → View mode
[New] button → Create mode (empty form)
[Edit] button → Edit mode (selected record in editable form)
[Delete] button → Delete confirmation → Refetch list
```

#### Features:
- Modal toolbar with all buttons visible
- Simple list table with action buttons
- Form view for create/edit/view modes
- Integrated delete confirmation
- Error handling and user feedback via toast notifications

### 5. **App Layout Update**

**File**: `src/App.tsx`

#### Changes:
- ✅ Wrapped `AppRouter` with `FormModeProvider`
- ✅ All pages now have access to form mode context

```typescript
<QueryClientProvider client={queryClient}>
  <FormModeProvider>
    <AppRouter />
  </FormModeProvider>
  <Toaster position="top-right" />
</QueryClientProvider>
```

## Comparison: V1 vs V2

| Feature | V1 | V2 |
|---------|----|----|
| Button Layout | Dropdown menu | 5 visible buttons |
| Selection | Multi-select with checkboxes | Single record |
| Search | Multi-field with pagination | Single field + input |
| Mode Tracking | None | Explicit (create/view/edit) |
| Form State | None | Context-based |
| Record Management | Bulk operations | Single record operations |
| User Intent Clarity | Hidden in dropdown | Visible button states |
| Learning Curve | Moderate | Low |
| Accessibility | Fair | Good (explicit buttons) |

## Integration Points

### Where to Use FormModeContext:
1. **Pages** - Track page mode (list/create/view/edit)
2. **Forms** - Enable/disable fields based on mode
3. **Navigation** - Route based on mode changes
4. **API Calls** - Create, update, delete operations

### Where to Use Toolbar:
1. **Feature pages** - Every feature list page should have it
2. **As wrapper** - Can wrap form components
3. **Callback handling** - Each page implements onSearch, onNew, onEdit, onDelete

### Where to Use SearchDialog:
1. **Toolbar** - Automatically opened by Toolbar component
2. **Standalone** - Can be used in other contexts for record selection
3. **Multi-entity** - Different SearchConfig per feature

## Example: Implementing for a New Feature

```typescript
// 1. Create search config for the feature
const FEATURE_SEARCH_CONFIG: SearchConfig = {
  title: 'Search Feature',
  endpoint: '/feature',
  fields: [
    { id: 'id', label: 'ID', type: 'text' },
    { id: 'name', label: 'Name', type: 'text' },
  ],
  displayFields: ['id', 'name'],
};

// 2. Use in page component
export function FeatureListPage() {
  const { selectedRecord, enterView, enterCreate, enterEdit, exitForm } = useFormMode();
  const [pageMode, setPageMode] = useState<'list' | 'create' | 'view' | 'edit'>('list');

  // 3. Implement handlers
  const handleSearchSelect = (record) => {
    enterView(record);
    setPageMode('view');
  };

  const handleNew = () => {
    enterCreate();
    setPageMode('create');
  };

  // 4. Render Toolbar
  return (
    <Toolbar
      hasSelection={!!selectedRecord}
      selectedRecord={selectedRecord}
      pageMode={pageMode}
      searchConfig={FEATURE_SEARCH_CONFIG}
      allRecords={records}
      onSearch={handleSearchSelect}
      onNew={handleNew}
      onEdit={() => { enterEdit(selectedRecord); setPageMode('edit'); }}
      onDelete={handleDelete}
      onModeChange={setPageMode}
    />
  );
}
```

## Migration Guide for Existing Features

For each feature that had old Toolbar/Search:

1. **Update imports**:
   ```typescript
   import { Toolbar } from '@/shared/components/ui/Toolbar';
   import { useFormMode } from '@/core/application/context/FormModeContext';
   ```

2. **Add useFormMode hook**:
   ```typescript
   const { selectedRecord, enterCreate, enterView, enterEdit, exitForm } = useFormMode();
   ```

3. **Create SearchConfig**:
   ```typescript
   const SEARCH_CONFIG: SearchConfig = { /* config */ };
   ```

4. **Add page mode state**:
   ```typescript
   const [pageMode, setPageMode] = useState<'list' | 'create' | 'view' | 'edit'>('list');
   ```

5. **Implement handlers and replace old Toolbar** with new one

6. **Remove old multi-select UI** (checkboxes, bulk action dropdowns)

## Files Created/Modified

### Created:
- ✅ `src/shared/components/ui/Toolbar_new.tsx` → `src/shared/components/ui/Toolbar.tsx`
- ✅ `src/core/application/context/FormModeContext.tsx` (NEW)

### Modified:
- ✅ `src/shared/components/ui/SearchDialog.tsx` (Simplified)
- ✅ `src/features/usuarios/presentation/pages/UsuariosListPage.tsx` (V2)
- ✅ `src/App.tsx` (FormModeProvider wrapper)

### Pre-existing (not changed):
- `src/shared/types/search.ts` (SearchConfig still works, entityName removed)

## Status

✅ **COMPLETE** - All V2 requirements implemented:
- [x] Visible buttons side-by-side (no dropdowns)
- [x] Single record selection (not multi-select)
- [x] Simplified search (1 field selector + 1 input)
- [x] Form mode states (create/view/edit)
- [x] FormModeContext for state management
- [x] Updated Usuarios example page
- [x] App wrapper with FormModeProvider
- [x] Type-safe TypeScript implementation

## Testing Checklist

- [ ] Start app and verify no errors
- [ ] Test Toolbar button visibility and states
- [ ] Test Search dialog field selection and filtering
- [ ] Test single record selection from search
- [ ] Test form mode transitions (create → view → edit)
- [ ] Test delete with confirmation dialog
- [ ] Test error handling and toast notifications
- [ ] Test FormModeContext state persistence
- [ ] Test page navigation with form mode changes
- [ ] Test with other features (adapt example pattern)

---

**Version**: 2.0  
**Date**: December 31, 2024  
**Status**: Ready for Integration Testing  
**Compatibility**: React 19.2.0, TypeScript, Zustand, TanStack Query
