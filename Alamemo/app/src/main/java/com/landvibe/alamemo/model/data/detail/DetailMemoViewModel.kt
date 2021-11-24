package com.landvibe.alamemo.model.data.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.AlamemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailMemoViewModel(val repository: AlamemoRepository): ViewModel() {

    val getDetailMemoByMemoIdRepositories = MutableLiveData<List<DetailMemo>>()
    val getDetailMemoByIdRepositories = MutableLiveData<DetailMemo>()
    val insertDetailMemoRepositories = MutableLiveData<Long>()
    val deleteDetailMemoRepositories = MutableLiveData<Long>()
    val deleteDetailMemoByIDRepositories = MutableLiveData<Long>()
    val deleteDetailMemoByMemoIDRepositories = MutableLiveData<Long>()
    val modifyDetailMemoRepositories = MutableLiveData<Long>()

     fun getDetailMemoByMemoId(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getDetailMemoByMemoId(memoId).let {
                detailMemoList -> getDetailMemoByMemoIdRepositories.postValue(detailMemoList)
            }
        }
    }

     fun getDetailMemoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getDetailMemoById(id).let {
                    detailMemo -> getDetailMemoByIdRepositories.postValue(detailMemo)
            }
        }
    }

     fun insertDetailMemo(detailMemo: DetailMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertDetailMemo(detailMemo).let {
                insertDetailMemoRepositories.postValue(System.currentTimeMillis())
            }
        }
    }
     fun deleteDetailMemo(detailMemo: DetailMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteDetailMemo(detailMemo).let {
                deleteDetailMemoRepositories.postValue(System.currentTimeMillis())
            }
        }
    }
     fun deleteDetailMemoByID(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteDetailMemoByID(id).let {
                deleteDetailMemoByIDRepositories.postValue(System.currentTimeMillis())
            }
        }
    }
     fun deleteDetailMemoByMemoID(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteDetailMemoByMemoID(memoId).let {
                deleteDetailMemoByMemoIDRepositories.postValue(System.currentTimeMillis())
            }
        }
    }
     fun modifyDetailMemo(id: Long, type: MutableLiveData<Int>, icon: MutableLiveData<String>,
                                 title: MutableLiveData<String>, scheduleDateYear: MutableLiveData<Int>,
                                 scheduleDateMonth: MutableLiveData<Int>, scheduleDateDay: MutableLiveData<Int>,
                                 scheduleDateHour: MutableLiveData<Int>, scheduleDateMinute: MutableLiveData<Int>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.modifyDetailMemo(id, type, icon, title, scheduleDateYear, scheduleDateMonth, scheduleDateDay, scheduleDateHour, scheduleDateMinute)
                .let {
                    modifyDetailMemoRepositories.postValue(System.currentTimeMillis())
                }
        }
    }
}