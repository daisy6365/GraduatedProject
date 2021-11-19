package com.example.graduatedproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chatroom.view.*

enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

interface ItemTouchHelperListener {
    fun onItemMove(from_position: Int, to_position: Int): Boolean
    fun onItemSwipe(position: Int)
    fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder)
    fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder)
}

open class ItemTouchHelperCallback : ItemTouchHelper.Callback {
    var listener: ItemTouchHelperListener
    var context: Context? = null
    var swipeBack = false
    private var buttonsShowedState = ButtonsState.GONE
    private val buttonWidth: Float = 250f
    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    constructor(listener: ItemTouchHelperListener, context: Context) {
        this.listener = listener
        this.context = context
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val drag_flags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipe_flags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(drag_flags, swipe_flags)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.adapterPosition)
    }

    //아이템을 터치하거나 스와이프하거나 뷰에 변화가 생길경우 불러오는 함수
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonsShowedState != ButtonsState.GONE) {
                var dd = dX
                if (buttonsShowedState == ButtonsState.LEFT_VISIBLE){
                    dd = Math.max(dX, buttonWidth)
                }
                if (buttonsShowedState == ButtonsState.RIGHT_VISIBLE) {
                    dd = Math.min(dX, -buttonWidth)
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dd,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            } else {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            if (buttonsShowedState == ButtonsState.GONE) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        currentItemViewHolder = viewHolder
        //버튼을 그려주는 함수
        drawButtons(c, currentItemViewHolder!!)
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithOutPadding: Float = buttonWidth - 10
        val buttonWidthWithOutPadding2: Float = buttonWidth - 10
        val corners = 5f

        val itemView = viewHolder.itemView
        val p = Paint()
        buttonInstance = null

        if (buttonsShowedState == ButtonsState.RIGHT_VISIBLE) {
            val rightButton = RectF(
                itemView.right - buttonWidthWithOutPadding,
                itemView.top + 10.toFloat(),
                itemView.right - 10.toFloat() ,
                itemView.bottom - 10.toFloat()
            )
            p.setColor(Color.rgb(230,101,101))
            c.drawRoundRect(rightButton, corners, corners, p)
            drawText("삭제", c, rightButton, p)
            buttonInstance = rightButton

        }
        else if(buttonsShowedState == ButtonsState.LEFT_VISIBLE){
            val leftButton = RectF(
                itemView.left + 10.toFloat(),
                itemView.top + 10.toFloat(),
                itemView.left + buttonWidthWithOutPadding,
                itemView.bottom - 10.toFloat()
            )
            p.setColor(Color.rgb(128,128,128))
            c.drawRoundRect(leftButton, corners, corners, p)
            drawText("수정", c, leftButton, p)
            buttonInstance = leftButton
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 30f
        p.setColor(Color.WHITE)
        p.isAntiAlias = true
        p.textSize = textSize

        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                swipeBack =
                    (event?.action == MotionEvent.ACTION_CANCEL) or (event?.action == MotionEvent.ACTION_UP)
                if (swipeBack) {
                    if (dX < -buttonWidth)
                        buttonsShowedState = ButtonsState.RIGHT_VISIBLE
                    else if (dX > buttonWidth){
                        buttonsShowedState = ButtonsState.LEFT_VISIBLE
                    }
                    if (buttonsShowedState != ButtonsState.GONE) {
                        setTouchDownListener(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                        setItemsClickable(recyclerView, false)
                    }
                }
                return false
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN)
                    setTouchUpListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                return false
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        return false
                    }
                })
                setItemsClickable(recyclerView, true)
                swipeBack = false

                if (listener != null && buttonInstance != null
                    && buttonInstance!!.contains(event!!.x, event!!.y)) {
                    if (buttonsShowedState == ButtonsState.RIGHT_VISIBLE) {
                        listener.onRightClick(viewHolder.adapterPosition, viewHolder)
                        buttonsShowedState = ButtonsState.GONE
                        currentItemViewHolder = null
                        Log.d(
                            "TEST",
                            "onSwiped: ${viewHolder.itemView.chatroom_name.text}DELETED!!!!!!!!"
                        )
                    }
                    else if (buttonsShowedState == ButtonsState.LEFT_VISIBLE){
                        listener.onLeftClick(viewHolder.adapterPosition, viewHolder)
                        buttonsShowedState = ButtonsState.GONE
                        currentItemViewHolder = null
                        Log.d(
                            "TEST",
                            "onSwiped: ${viewHolder.itemView.chatroom_name.text}MODIFY!!!!!!!!"
                        )
                    }
                }
                return false
            }
        })
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0..recyclerView.childCount - 1) {
            recyclerView.getChildAt(i).setClickable(isClickable)
        }
    }
}