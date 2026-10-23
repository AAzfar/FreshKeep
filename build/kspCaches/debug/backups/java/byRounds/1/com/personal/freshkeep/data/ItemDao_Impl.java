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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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

  private final SharedSQLiteStatement __preparedStmtOfUpdateAvailability;

  private final SharedSQLiteStatement __preparedStmtOfMarkItemPurchased;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfInventoryItem = new EntityInsertionAdapter<InventoryItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `inventory_items` (`id`,`barcode`,`name`,`category`,`expiryDateEpoch`,`quantity`,`rawJsonDetails`,`isAvailable`,`isOnBuyList`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
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
        final int _tmp = entity.isAvailable() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isOnBuyList() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
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
    this.__preparedStmtOfUpdateAvailability = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE inventory_items SET isAvailable = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkItemPurchased = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE inventory_items SET isOnBuyList = 0, isAvailable = 1, expiryDateEpoch = ? WHERE id = ?";
        return _query;
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
  public Object updateAvailability(final int itemId, final boolean isAvailable,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAvailability.acquire();
        int _argIndex = 1;
        final int _tmp = isAvailable ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, itemId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAvailability.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markItemPurchased(final int itemId, final long expiryDateEpoch,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkItemPurchased.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, expiryDateEpoch);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, itemId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkItemPurchased.release(_stmt);
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
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfIsOnBuyList = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnBuyList");
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
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final boolean _tmpIsOnBuyList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsOnBuyList);
            _tmpIsOnBuyList = _tmp_1 != 0;
            _item = new InventoryItem(_tmpId,_tmpBarcode,_tmpName,_tmpCategory,_tmpExpiryDateEpoch,_tmpQuantity,_tmpRawJsonDetails,_tmpIsAvailable,_tmpIsOnBuyList);
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
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfIsOnBuyList = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnBuyList");
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
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final boolean _tmpIsOnBuyList;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsOnBuyList);
            _tmpIsOnBuyList = _tmp_1 != 0;
            _result = new InventoryItem(_tmpId,_tmpBarcode,_tmpName,_tmpCategory,_tmpExpiryDateEpoch,_tmpQuantity,_tmpRawJsonDetails,_tmpIsAvailable,_tmpIsOnBuyList);
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

  @Override
  public Object addItemsToBuyList(final List<Integer> itemIds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE inventory_items SET isOnBuyList = 1 WHERE id IN (");
        final int _inputSize = itemIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (int _item : itemIds) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
