package com.egoriku.ladyhappy.data.repositories.datasource

import com.egoriku.ladyhappy.data.entities.CategoriesDocumentEntity
import com.egoriku.ladyhappy.data.entities.CategoryEntity
import com.egoriku.ladyhappy.data.repositories.base.BaseFirebaseDataSource
import com.egoriku.ladyhappy.firebase.RxFirestore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Observable
import javax.inject.Inject

class CategoriesDataSourceRemote
@Inject constructor(private val firebaseFirestore: FirebaseFirestore) : BaseFirebaseDataSource() {

    private lateinit var query: Query

    private fun getCategoriesReference(): Query {
        query = firebaseFirestore
                .collection(COLLECTION_KEY_CATEGORIES)
                .orderBy(QUERY_ID, Query.Direction.ASCENDING)
        return query
    }

    fun getCategories(): Observable<CategoriesDocumentEntity> {
        return getCategoriesReference().let {
            RxFirestore.getObservableCategories(it, CategoriesDocumentEntity(), CategoryEntity::class.java)
        }
    }
}