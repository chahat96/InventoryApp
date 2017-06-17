package com.example.chahat.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chahat.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    public byte[] image;
    Button imageBtn;
    ImageView imageView;
    Button addQuantity;
    Button reduceQuantity;
    Button saleItem;
    private Spinner mProductSizeSpinner;
    private Uri mCurrentProductUri;
    private EditText mProductNameEditText;
    private EditText mCompanyNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierEmailEditText;
    private int REQUEST_IMAGE = 1;
    private int mProductSize = InventoryEntry.PRODUCT_SIZE_UNKNOWN;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        imageBtn = (Button) findViewById(R.id.add_image);
        imageView = (ImageView) findViewById(R.id.image_view);
        saleItem = (Button) findViewById(R.id.saleItem);

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            saleItem.setEnabled(true);
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mCompanyNameEditText = (EditText) findViewById(R.id.edit_product_company);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mSupplierEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mProductSizeSpinner = (Spinner) findViewById(R.id.spinner_product_size);
        addQuantity = (Button) findViewById(R.id.add_unit);
        reduceQuantity = (Button) findViewById(R.id.reduce_unit);

        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = mQuantityEditText.getText().toString();
                int newValue;
                if (value.isEmpty()) {
                    newValue = 0;
                } else {
                    newValue = Integer.parseInt(value);
                }
                mQuantityEditText.setText(String.valueOf(newValue + 1));
            }
        });

        reduceQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = mQuantityEditText.getText().toString();
                int newValue;
                if (value.isEmpty()) {
                    newValue = 0;
                } else {
                    newValue = Integer.parseInt(value);
                }

                if (newValue - 1 >= 0)
                    newValue = newValue - 1;
                mQuantityEditText.setText(String.valueOf(newValue));
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE);
                }
            }
        });

        saleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = mQuantityEditText.getText().toString();
                int newValue;
                if (value.isEmpty()) {
                    newValue = 0;
                } else {
                    newValue = Integer.parseInt(value);
                }
                if (newValue - 1 >= 0)
                    newValue = newValue - 1;
                mQuantityEditText.setText(String.valueOf(newValue));
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mCompanyNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        mProductSizeSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView newImage = (ImageView) findViewById(R.id.image_view);
            newImage.setImageBitmap(imageBitmap);
            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            image = stream.toByteArray();
        }
    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter productSizeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_product_size_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        productSizeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mProductSizeSpinner.setAdapter(productSizeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mProductSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.product_size_xs))) {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_EXTRA_SMALL;
                    } else if (selection.equals(getString(R.string.product_size_s))) {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_SMALL;
                    } else if (selection.equals(getString(R.string.product_size_m))) {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_MEDIUM;
                    } else if (selection.equals(getString(R.string.product_size_l))) {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_LARGE;
                    } else if (selection.equals(getString(R.string.product_size_xl))) {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_EXTRA_LARGE;
                    } else {
                        mProductSize = InventoryEntry.PRODUCT_SIZE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProductSize = InventoryEntry.PRODUCT_SIZE_UNKNOWN;
            }
        });
    }


    public void saveProduct() {

        String productNameString = mProductNameEditText.getText().toString().trim();
        String companyNameString = mCompanyNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierString = mSupplierEmailEditText.getText().toString().trim();
        ImageView mProductImageView = (ImageView) findViewById(R.id.image_view);

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(companyNameString) && mProductSize == InventoryEntry.PRODUCT_SIZE_UNKNOWN && image == null) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_COMPANY, companyNameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SIZE, mProductSize);
        // If the price,quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, image);


        if (mCurrentProductUri == null) {
            if (productNameString.matches("")) {
                Toast.makeText(EditorActivity.this, "Please enter a Product Name and other information to save data. ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (companyNameString.matches("")) {
                Toast.makeText(EditorActivity.this, "Please Enter Company Information and other information to add product.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantityString.matches("")) {
                Toast.makeText(EditorActivity.this, "Please enter quantity of product and other information to add product.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (priceString.matches("")) {
                Toast.makeText(EditorActivity.this, "Please enter price of product and other information to add product.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (supplierString.matches("")) {
                Toast.makeText(EditorActivity.this, "Please enter supplier email-Id and other information to add product.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (image == null) {
                Toast.makeText(EditorActivity.this, "Please add a Product Image and other information to add product.", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            MenuItem menuItem1 = menu.findItem(R.id.action_order);
            menuItem.setVisible(false);
            menuItem1.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_order:
                orderSummary();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void orderSummary() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mSupplierEmailEditText.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "For new order");
        String bodyMessage = "I want to order more of these Items " +
                mProductNameEditText.getText().toString().trim();
        intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
        startActivity(intent);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_COMPANY,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_PRODUCT_SIZE,
                InventoryEntry.COLUMN_PRODUCT_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,     // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int companyColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_COMPANY);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int sizeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SIZE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String company = cursor.getString(companyColumnIndex);
            String supplierEmail = cursor.getString(supplierColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int size = cursor.getInt(sizeColumnIndex);
            byte[] productImage = cursor.getBlob(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(name);
            mCompanyNameEditText.setText(company);
            mSupplierEmailEditText.setText(supplierEmail);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));

            switch (size) {
                case InventoryEntry.PRODUCT_SIZE_EXTRA_SMALL:
                    mProductSizeSpinner.setSelection(1);
                    break;
                case InventoryEntry.PRODUCT_SIZE_SMALL:
                    mProductSizeSpinner.setSelection(2);
                    break;
                case InventoryEntry.PRODUCT_SIZE_MEDIUM:
                    mProductSizeSpinner.setSelection(3);
                    break;
                case InventoryEntry.PRODUCT_SIZE_EXTRA_LARGE:
                    mProductSizeSpinner.setSelection(4);
                    break;
                case InventoryEntry.PRODUCT_SIZE_LARGE:
                    mProductSizeSpinner.setSelection(5);
                    break;
                default:
                    mProductSizeSpinner.setSelection(0);
                    break;
            }
            if (productImage != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);
                imageView.setImageBitmap(bitmap);
            }
            image = productImage;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mCompanyNameEditText.setText("");
        mSupplierEmailEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mProductSizeSpinner.setSelection(0);

    }

}

