package com.personal.freshkeep.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ItemDao_Impl implements ItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<InventoryItem> __insertionAdapterOfInventoryItem;

  private final EntityDeletionOrUpdateAdapter<InventoryItem> __deletionAdapterOfInventoryItem;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInventoryItem = new EntityInsertionAdapter<InventoryItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `inventory_items` (`id`,`barcode`,`name`,`category`,`expiryDateEpoch`,`quantity`,`rawJsonDetails`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InventoryItem entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getBarcode() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getBarcode());
        }
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getCategory());
        statement.bindLong(5, entity.getExpiryDateEpoch());
        statement.bindLong(6, entity.getQuantity());
        if (entity.getRawJsonDetails() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getRawJsonDetails());
        }
      }
    };
    this.__deletionAdapterOfInventoryItem = new EntityDeletionOrUpdateAdapter<InventoryItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `inventory_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InventoryItem entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insertOrUpdateItem(final InventoryItem item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInventoryItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItem(final InventoryItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfInventoryItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<InventoryItem>> getAllItemsSortedByExpiry() {
    final String _sql = "SELECT * FROM inventory_items ORDER BY expiryDateEpoch ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"inventory_items"}, new Callable<List<InventoryItem>>() {
      @Override
      @NonNull
      public List<InventoryItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfExpiryDateEpoch = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDateEpoch");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfRawJsonDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "rawJsonDetails");
          final List<InventoryItem> _result = new ArrayList<InventoryItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InventoryItem _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpBarcode;
            if (_cursor.isNull(_cursorIndexOfBarcode)) {
              _tmpBarcode = null;
            } else {
              _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            }
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final long _tmpExpiryDateEpoch;
            _tmpExpiryDateEpoch = _cursor.getLong(_cursorIndexOfExpiryDateEpoch);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpRawJsonDetails;
            if (_cursor.isNull(_cursorIndexOfRawJsonDetails)) {
              _tmpRawJsonDetails = null;
            } else {
              _tmpRawJsonDetails = _cursor.getString(_cursorIndexOfRawJsonDetails);
            }
            _item = new InventoryItem(_tmpId,_tmpBarcode,_tmpName,_tmpCategory,_tmpExpiryDateEpoch,_tmpQuantity,_tmpRawJsonDetails);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getItemByBarcode(final String barcode,
      final Continuation<? super InventoryItem> $completion) {
    final String _sql = "SELECT * FROM inventory_items WHERE barcode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, barcode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<InventoryItem>() {
      @Override
      @Nullable
      public InventoryItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBarcode = CursorUtil.getColumnIndexOrThrow(_cursor, "barcode");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfExpiryDateEpoch = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDateEpoch");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfRawJsonDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "rawJsonDetails");
          final InventoryItem _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpBarcode;
            if (_cursor.isNull(_cursorIndexOfBarcode)) {
              _tmpBarcode = null;
            } else {
              _tmpBarcode = _cursor.getString(_cursorIndexOfBarcode);
            }
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final long _tmpExpiryDateEpoch;
            _tmpExpiryDateEpoch = _cursor.getLong(_cursorIndexOfExpiryDateEpoch);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpRawJsonDetails;
            if (_cursor.isNull(_cursorIndexOfRawJsonDetails)) {
              _tmpRawJsonDetails = null;
            } else {
              _tmpRawJsonDetails = _cursor.getString(_cursorIndexOfRawJsonDetails);
            }
            _result = new InventoryItem(_tmpId,_tmpBarcode,_tmpName,_tmpCategory,_tmpExpiryDateEpoch,_tmpQuantity,_tmpRawJsonDetails);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
