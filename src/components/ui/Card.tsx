import { ReactNode } from 'react';
import { cn } from '@/lib/utils';

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  onClick?: () => void;
}

export default function Card({ children, className, hover, onClick }: CardProps) {
  return (
    <div
      className={cn('glass p-5', hover && 'card-hover cursor-pointer', className)}
      onClick={onClick}
    >
      {children}
    </div>
  );
}
