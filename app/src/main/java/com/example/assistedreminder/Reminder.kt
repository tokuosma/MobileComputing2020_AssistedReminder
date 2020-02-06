package com.example.assistedreminder

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "reminders")
data class Reminder (
    @PrimaryKey(autoGenerate = true) var uid:Int?,
    @ColumnInfo(name="time") var time : Long?,
    @ColumnInfo(name="location")var location: String?,
    @ColumnInfo(name="message") var message: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(uid)
        parcel.writeValue(time)
        parcel.writeString(location)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}

@Dao
interface ReminderDao{
    @Transaction @Insert
    suspend fun insert(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY time ASC")
    fun getReminders(): LiveData<List<Reminder>>

    @Query("DELETE  FROM reminders")
    suspend fun deleteAll()

}

