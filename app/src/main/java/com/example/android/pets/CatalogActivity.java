/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks <Cursor> {

    ListView listView;
    PetCursorAdapter petCursorAdapter;

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName( );

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        View emptyView = findViewById(R.id.empty_view);
        listView = (ListView) findViewById(R.id.list);

        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener( ) {
            @Override
            public void onItemClick( AdapterView <?> parent, View view, int position, long id ) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        petCursorAdapter = new PetCursorAdapter(this, null);
        getLoaderManager( ).initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater( ).inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId( )) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insetData( );
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
    }

    private void insetData() {

        ContentValues values = new ContentValues( );
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insere um novo pet no provider, returnando o URI de conteúdo para o novo pet.
        Uri newUri = getContentResolver( ).insert(PetEntry.CONTENT_URI, values);

        // Mostra um mensagem toast dependendo ou não se a inserção foi bem sucedida
        if (newUri == null) {
            // Se o novo conteúdo do URI é nulo, então houve um erro com inserção.
            Toast.makeText(this, R.string.insert_failed,
                    Toast.LENGTH_SHORT
            ).show( );
        } else {
            // Caso contrário, a inserção foi bem sucedida e podemos mostrar um toast.
            Toast.makeText(this, R.string.insert_ok,
                    Toast.LENGTH_SHORT
            ).show( );
        }
    }

    @Override
    public Loader <Cursor> onCreateLoader( int id, Bundle args ) {

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};

        return new CursorLoader(
                this,
                PetEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }

    @Override
    public void onLoadFinished( Loader <Cursor> loader, Cursor data ) {
        petCursorAdapter.swapCursor(data);
        listView.setAdapter(petCursorAdapter);
    }

    @Override
    public void onLoaderReset( Loader <Cursor> loader ) {
        petCursorAdapter.swapCursor(null);
    }
}
