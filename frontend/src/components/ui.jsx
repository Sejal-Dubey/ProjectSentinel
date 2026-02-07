import React from 'react';
import { cn } from '../lib/utils';

export function Card({ className, children }) {
    return (
        <div className={cn(
            "bg-slate-900/40 backdrop-blur-xl border border-white/10 rounded-2xl p-6 shadow-2xl relative overflow-hidden group hover:border-white/20 transition-all duration-300",
            className
        )}>
            <div className="absolute inset-0 bg-gradient-to-br from-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none" />
            {children}
        </div>
    );
}

export function Badge({ children, variant = "default" }) {
    const variants = {
        default: "bg-blue-500/10 text-blue-400 border-blue-500/20 shadow-[0_0_10px_rgba(59,130,246,0.2)]",
        success: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20 shadow-[0_0_10px_rgba(16,185,129,0.2)]",
        warning: "bg-amber-500/10 text-amber-400 border-amber-500/20",
        purple: "bg-purple-500/10 text-purple-400 border-purple-500/20 shadow-[0_0_10px_rgba(168,85,247,0.2)]",
    };

    return (
        <span className={cn("px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider border", variants[variant])}>
            {children}
        </span>
    );
}
