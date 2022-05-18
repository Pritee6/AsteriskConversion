package com.example.asteriskconversion

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.text.style.UpdateLayout
import android.view.View
import java.lang.ref.WeakReference

//private const val DOT = '＊'
private const val DOT = '*'

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {

    private val ACTIVE: Any = NoCopySpan.Concrete()

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        if (source is Spannable) {
            val vr = source.getSpans(0, source.length, ViewReference::class.java)
            vr.forEach { source.removeSpan(it) }

            removeVisibleSpans(source)
            source.setSpan(ViewReference(view!!), 0, 0, Spannable.SPAN_POINT_POINT)
        }
        return PasswordCharSequence(source!!)
    }

    private fun removeVisibleSpans(sp: Spannable) {
        val old = sp.getSpans(0, sp.length, Visible::class.java)
        old.forEach { sp.removeSpan(it) }
    }

    private inner class PasswordCharSequence(private val mSource: CharSequence) : CharSequence, GetChars {

        override val length: Int
            get() = mSource.length

        override fun get(index: Int): Char {
            if (mSource is Spanned) {
                var st = mSource.getSpanStart(ACTIVE)
                var en = mSource.getSpanEnd(ACTIVE)

                if (index in st until en) return mSource[index]

                val visible = mSource.getSpans(0, mSource.length, Visible::class.java)
                visible.forEach {
                    if (mSource.getSpanStart(it.mTransformer) >= 0) {
                        st = mSource.getSpanStart(it)
                        en = mSource.getSpanEnd(it)
                        if (index in st until en) return mSource[index]
                    }
                }
            }
            return DOT
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            val buf = CharArray(endIndex - startIndex)
            getChars(startIndex, endIndex, buf, 0)
            return String(buf)
        }

        override fun toString(): String = subSequence(0, length).toString()

        override fun getChars(start: Int, end: Int, dest: CharArray?, destoff: Int) {
            TextUtils.getChars(mSource, start, end, dest, destoff)

            var st = -1
            var en = -1
            var nvisible = 0
            var starts: IntArray? = null
            var ends: IntArray? = null

            if (mSource is Spanned) {
                st = mSource.getSpanStart(ACTIVE)
                en = mSource.getSpanEnd(ACTIVE)

                val visible = mSource.getSpans(0, mSource.length, Visible::class.java)
                nvisible = visible.size
                starts = IntArray(nvisible)
                ends = IntArray(nvisible)

                for (i in 0 until nvisible) {
                    if (mSource.getSpanStart(visible[i].mTransformer) >= 0) {
                        starts[i] = mSource.getSpanStart(visible[i])
                        ends[i] = mSource.getSpanEnd(visible[i])
                    }
                }
            }

            for (i in start until end) {
                if (i !in st until en) {
                    var visible = false

                    for (a in 0 until nvisible) {
                        if (i >= starts!![a] && i < ends!![a]) {
                            visible = true
                            break
                        }
                    }

                    if (!visible) dest?.set(i - start + destoff, DOT)
                }
            }
        }
    }

    private inner class Visible(private val mText: Spannable, val mTransformer: AsteriskPasswordTransformationMethod) : Handler(
        Looper.getMainLooper()), UpdateLayout, Runnable {
        init {
            postAtTime(this, SystemClock.uptimeMillis() + 1000)
        }

        override fun run() {
            mText.removeSpan(this)
        }
    }
    private inner class ViewReference(v: View) : WeakReference<View>(v), NoCopySpan

}